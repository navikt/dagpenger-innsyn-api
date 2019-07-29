package no.nav.dagpenger.innsyn.restapi

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import no.nav.dagpenger.innsyn.JwtStub
import no.nav.dagpenger.innsyn.lookup.InnsynProducer
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.Configuration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InntektRouteTest {

    private val config = Configuration()

    private val jwtStub = JwtStub(config.application.jwksIssuer)
    private val token = jwtStub.createTokenFor("user")

    @Test
    fun `Valid request to inntekt endpoint should succeed and produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns true
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                jwkProvider = jwtStub.stubbedJwkProvider())) {
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