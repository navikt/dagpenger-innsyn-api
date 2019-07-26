package integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.dagpenger.innsyn.lookup.getGjeldendeAktoerIDFromIDToken
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AktoerIDFromIDTokenTest {
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
    val testIdToken = "1234"

    WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/"))
                    .withHeader("Nav-Personidenter", WireMock.equalTo(testFnr))
                    .willReturn(WireMock.aResponse().withBody(validJsonBody))
    )

    val aktoerID = getGjeldendeAktoerIDFromIDToken(testIdToken, testFnr, server.url(""))
    assertEquals(testAktoerID, aktoerID)
}

@Test
fun `No aktoerId in response from call to Aktørregisteret`() {
    val testFnr = "12345678912"
    val testIdToken = "1234"

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/"))
            .withHeader("Nav-Personidenter", WireMock.equalTo(testFnr))
            .willReturn(WireMock.aResponse().withBody(validJsonBodyWithEmptyIdenter))
    )

    val aktoerId = getGjeldendeAktoerIDFromIDToken(testIdToken, testFnr, server.url(""))
    assertEquals("", aktoerId)
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
        "feilmelding": null
    }
}
""".trimIndent()
}