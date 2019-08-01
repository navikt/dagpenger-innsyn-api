package no.nav.dagpenger.innsyn.lookup

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import kotlinx.coroutines.TimeoutCancellationException
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths
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

        getInntekt(
            kafkaProducer = kafkaMock,
            packetStore = storeMock,
            behov = behov,
            brønnøysundLookup = mockContainer.brønnøysundLookup
        )

        verifyAll {
            kafkaMock.produceEvent(behov)
            // TODO: Uncomment in production
            //storeMock.get(slot.toString())
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
            getInntekt(
                    kafkaProducer = kafkaMock,
                    packetStore = storeMock,
                    behov = behov,
                    brønnøysundLookup = mockContainer.brønnøysundLookup,
                    timeout = 1000
            )
        }

        verifyAll {
            storeMock.get(slot.toString()) wasNot Called
        }
    }
}

private object mockContainer {
    private val DOCKER_PATH = Paths.get("aktoer-mock/")

    class KGenericContainer : GenericContainer<KGenericContainer>(ImageFromDockerfile()
            .withFileFromPath(".", DOCKER_PATH)
            .withDockerfilePath("./Dockerfile.ci"))

    private val instance by lazy {
        KGenericContainer().apply {
            withExposedPorts(3050)
            start()
        }
    }
    private val aktørURL = "http://" + instance.containerIpAddress + ":" + instance.getMappedPort(3050) + "/aktoerregister/api/v1/identer"
    private val brURL = "http://" +
            mockContainer.instance.containerIpAddress +
            ":" +
            mockContainer.instance.getMappedPort(3050) +
            "/br/"

    val aktoerRegister = AktørregisterLookup(url = aktørURL)

    val brønnøysundLookup = BrønnøysundLookup(url = brURL)
}