package no.nav.dagpenger.innsyn.lookup

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.dagpenger.innsyn.JwtStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class AktørregisterLookupTest {

    val jwtStub = JwtStub()
    private val token = jwtStub.createTokenFor("user")

companion object {
    val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

    @BeforeAll
    @JvmStatic
    fun start() {
        server.start()
    }

    @AfterAll
    @JvmStatic
    fun stop() {
        server.stop()
    }
}

@BeforeEach
fun configure() {
    WireMock.configureFor(server.port())
}

@Test
fun `Successful fetch of aktoerId`() {
    val testFnr = "12345678912"
    val testAktoerID = "1234567891234"

    WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/"))
                    .withHeader("Nav-Personidenter", WireMock.equalTo(testFnr))
                    .willReturn(WireMock.aResponse().withBody(validJsonBody))
    )

    val aktoerID = AktørregisterLookup(url = server.url("")).getGjeldendeAktørIDFromIDToken(token, testFnr)
    assertEquals(testAktoerID, aktoerID)
}

@Test
fun `No aktørId in Aktørregisteret response should throw exception`() {
    val testFnr = "12345678912"

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/"))
            .withHeader("Nav-Personidenter", WireMock.equalTo(testFnr))
            .willReturn(WireMock.aResponse().withBody(validJsonBodyWithEmptyIdenter))
    )

    assertThrows<AktørIdNotFoundException> {
        AktørregisterLookup(url = server.url("")).getGjeldendeAktørIDFromIDToken(token, testFnr)
    }
}

val validJsonBody = """
{
    "12345678912": {
        "identer": [
            {
                "ident": "1234567891234",
                "identgruppe": "AktoerId",
                "gjeldende": true
            }
        ],
        "feilmelding": null
    }
}
""".trimIndent()

val validJsonBodyWithEmptyIdenter = """
{
    "12345678912": {
        "identer": [
        ],
        "feilmelding": "Feilet"
    }
}
""".trimIndent()
}