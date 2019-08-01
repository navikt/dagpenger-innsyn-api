package no.nav.dagpenger.innsyn.routing

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.dagpenger.innsyn.JwtStub
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.BehovProducer
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.Configuration
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InntektRouteTest {

    private val config = Configuration()
    private val jwtStub = JwtStub(config.application.jwksIssuer)

    private val token = jwtStub.createTokenFor(config.application.oidcUser)

    @Test
    fun `Valid request to inntekt endpoint should succeed`() {

        val kafkaMock = mockk<BehovProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns true
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
            kafkaProducer = kafkaMock,
            packetStore = storeMock,
            jwkProvider = jwtStub.stubbedJwkProvider(),
            aktørregisterLookup = mockContainer.aktoerRegister,
            brønnøysundLookup = mockContainer.brønnøysundLookup)
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(requestHandled)
            }
        }
    }

    @Test
    fun `504 response on timeout`() {

        val kafkaMock = mockk<BehovProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
            kafkaProducer = kafkaMock,
            packetStore = storeMock,
            jwkProvider = jwtStub.stubbedJwkProvider(),
            aktørregisterLookup = mockContainer.aktoerRegister,
            brønnøysundLookup = mockContainer.brønnøysundLookup)
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.GatewayTimeout, response.status())
                assertTrue(requestHandled)
            }
        }
    }

    @Test
    fun `Request missing ID token should be unauthorized`() {
        withTestApplication(MockApi(
            jwkProvider = jwtStub.stubbedJwkProvider())) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl)
                .apply { assertEquals(HttpStatusCode.Unauthorized, response.status()) }
        }
    }

    @Test
    fun `Request with invalid ID token should be unauthorized`() {
        val anotherIssuer = JwtStub("https://anotherissuer")
        val cookie = "ID_token=${anotherIssuer.createTokenFor("user")}"

        withTestApplication(MockApi(
            jwkProvider = jwtStub.stubbedJwkProvider())) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply { assertEquals(HttpStatusCode.Unauthorized, response.status()) }
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
