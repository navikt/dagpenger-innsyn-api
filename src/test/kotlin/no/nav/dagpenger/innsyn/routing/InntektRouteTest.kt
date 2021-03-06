package no.nav.dagpenger.innsyn.routing

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.events.Problem
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.JwtStub
import no.nav.dagpenger.innsyn.lookup.BehovProducer
import no.nav.dagpenger.innsyn.lookup.InntektLookup
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.settings.Configuration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val aktørResponse = "18128126178"

class InntektRouteTest {

    private val config = Configuration()
    private val jwtStub = JwtStub(config.application.jwksIssuer)

    private val token = jwtStub.createTokenFor(config.application.oidcUser)

    @Test
    fun `Valid request to inntekt endpoint should succeed`() {

        val cookie = "ID_token=$token"

        val aktørregisterLookupMock = mockk<AktørregisterLookup>(relaxed = true).apply {
            every { this@apply.getGjeldendeAktørIDFromIDToken(any(), any()) } returns aktørResponse
        }

        val inntektMock = mockk<InntektLookup>(relaxed = true)

        withTestApplication(MockApi(
            jwkProvider = jwtStub.stubbedJwkProvider(),
            aktørregisterLookup = aktørregisterLookupMock,
            inntektLookup = inntektMock)
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

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(any()) } returns false
        }

        val aktørregisterLookupMock = mockk<AktørregisterLookup>(relaxed = true).apply {
            every { this@apply.getGjeldendeAktørIDFromIDToken(any(), any()) } returns aktørResponse
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
            jwkProvider = jwtStub.stubbedJwkProvider(),
            aktørregisterLookup = aktørregisterLookupMock,
            inntektLookup = InntektLookup(kafkaMock, storeMock, mockk()))
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

    @Test
    fun `Should respond on unhandled errors`() {
        val cookie = "ID_token=$token"
        val aktørregisterLookupMock = mockk<AktørregisterLookup>(relaxed = true).apply {
            every { this@apply.getGjeldendeAktørIDFromIDToken(any(), any()) } throws Exception("")
        }

        withTestApplication(MockApi(
                jwkProvider = jwtStub.stubbedJwkProvider(),
                aktørregisterLookup = aktørregisterLookupMock
        )) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.InternalServerError, response.status())
                val problem = moshiInstance.adapter(Problem::class.java).fromJson(response.content!!)
                assertEquals("Uhåndtert feil!", problem?.title)
                assertEquals(500, problem?.status)
            }
        }
    }
}
