package no.nav.dagpenger.innsyn.restapi

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import no.nav.dagpenger.innsyn.JwtStub
import no.nav.dagpenger.innsyn.lookup.AktoerRegisterLookup
import no.nav.dagpenger.innsyn.lookup.InnsynProducer
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.Configuration
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InntektRouteTest {

    companion object {
        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        val env = KDockerComposeContainer(File("..${File.separator}docker-compose.yml"))
    }

    private val config = Configuration()

    private val jwtStub = JwtStub(config.application.jwksIssuer)
    private val token = jwtStub.createTokenFor(config.application.oidcUser)

    @Test
    fun `Valid request to inntekt endpoint should succeed and produce an event to Kafka`() {
        env.withExposedService("mockserver", 3050)
        println(env.getServiceHost("mockserver", 3050))

        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns true
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                jwkProvider = jwtStub.stubbedJwkProvider(),
                aktoerRegisterLookup = AktoerRegisterLookup("http://" + env.getServiceHost("mockserver", 3050) + ":3050/aktoerregister/api/v1/identer"))
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(requestHandled)
            }
        }

        verifyAll {
            kafkaMock.produceEvent(any())
        }
    }

    @Test
    fun `504 response on timeout`() {

        env.withExposedService("mockserver", 3050)
        println(env.getServiceHost("mockserver", 3050))

        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                jwkProvider = jwtStub.stubbedJwkProvider(),
                aktoerRegisterLookup = AktoerRegisterLookup("http://" + env.getServiceHost("mockserver", 3050) + ":3050/aktoerregister/api/v1/identer"))
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.GatewayTimeout, response.status())
                assertTrue(requestHandled)
            }
        }

        verifyAll {
            storeMock.get(slot.toString()) wasNot Called
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
