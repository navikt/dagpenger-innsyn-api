package no.nav.dagpenger.innsyn.lookup

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import kotlinx.coroutines.TimeoutCancellationException
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class InntektLookupTest {

    private val kafkaMock = mockk<BehovProducer>(relaxed = true)

    @Test
    fun `Produces an event to kafka and returns inntekt`() {

        val packetJson = """
            {
                "aktørId": "12345",
                "vedtakId": 123,
                "beregningsDato": 2019-01-25,
                "spesifisertInntektV1": {
                    "inntektId": {
                        "id": "01D8G6FS9QGRT3JKBTA5KEE64C"
                    },
                    "avvik": [],
                    "posteringer": [],
                    "ident": {
                        "aktørType": "NATURLIG_IDENT",
                        "identifikator": "-1"
                    },
                    "manueltRedigert": false,
                    "timestamp": "2019-08-02T10:20:32.996314"
                }
            }
        """.trimIndent()
        val packet = Packet(packetJson)

        val storeMock = mockk<PacketStore>().apply {
            every { this@apply.isDone(any()) } returns false andThen true
            every { this@apply.get(any())} returns packet
        }

        val brønnøysundLookupMock = mockk<BrønnøysundLookup>(relaxed = true)

        val behov = Behov(aktørId = "1", beregningsDato = LocalDate.now())

        InntektLookup(kafkaMock, storeMock, brønnøysundLookupMock)
                .getInntekt(behov = behov)

        verifyAll {
            kafkaMock.produceEvent(behov)
            // TODO: Uncomment in production
            // storeMock.get(slot.toString())
        }
    }

    @Test
    fun `Throws exception if kafka times out`() {
        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false
        }

        val brønnøysundLookupMock = mockk<BrønnøysundLookup>(relaxed = true)

        val behov = Behov(aktørId = "1", beregningsDato = LocalDate.now())

        assertThrows<TimeoutCancellationException> {
            InntektLookup(kafkaMock, storeMock, brønnøysundLookupMock)
                    .getInntekt(behov = behov, timeout = 1000)
        }

        verifyAll {
            storeMock.get(slot.toString()) wasNot Called
        }
    }
}
