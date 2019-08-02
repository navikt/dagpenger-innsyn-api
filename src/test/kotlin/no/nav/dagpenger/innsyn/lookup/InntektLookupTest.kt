package no.nav.dagpenger.innsyn.lookup

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import kotlinx.coroutines.TimeoutCancellationException
import no.nav.dagpenger.innsyn.MockContainer
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class InntektLookupTest {

    private val kafkaMock = mockk<BehovProducer>(relaxed = true)

    @Test
    fun `Produces an event to kafka and returns inntekt`() {
        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false andThen true
        }

        val behov = Behov(aktørId = "1", beregningsDato = LocalDate.now())

        InntektLookup(kafkaMock, storeMock, MockContainer.brønnøysundLookup)
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

        val behov = Behov(aktørId = "1", beregningsDato = LocalDate.now())

        assertThrows<TimeoutCancellationException> {
            InntektLookup(kafkaMock, storeMock, MockContainer.brønnøysundLookup)
                    .getInntekt(behov = behov, timeout = 1000)
        }

        verifyAll {
            storeMock.get(slot.toString()) wasNot Called
        }
    }
}
