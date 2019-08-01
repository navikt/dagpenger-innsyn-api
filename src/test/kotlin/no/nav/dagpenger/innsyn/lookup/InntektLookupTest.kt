package no.nav.dagpenger.innsyn.lookup

import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.expectedFinalResult
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class InntektLookupTest {

    @Test
    fun `Successfully produce an event to kafka and return expected inntekt`() {
        val kafkaMock = mockk<BehovProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false andThen true
        }

        val behov = Behov(aktørId = "1", beregningsDato = LocalDate.now())

        withTestApplication { getInntekt(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                behov = behov,
                brønnøysundLookup = mockk()
        ) }.apply {
            assertEquals(
                    moshiInstance.adapter(UserInformation::class.java).toJson(expectedFinalResult),
                    this)
        }

        verifyAll {
            kafkaMock.produceEvent(behov)
            // TODO: Uncomment in production
            //storeMock.get(slot.toString())
        }
    }
}