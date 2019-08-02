package no.nav.dagpenger.innsyn

import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths

object MockContainer {
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
            MockContainer.instance.containerIpAddress +
            ":" +
            MockContainer.instance.getMappedPort(3050) +
            "/br/"

    val aktoerRegister = AktørregisterLookup(url = aktørURL)

    val brønnøysundLookup = BrønnøysundLookup(url = brURL)
}