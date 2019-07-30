package no.nav.dagpenger.innsyn.lookup

import khttp.responses.Response
import mu.KLogger
import mu.KotlinLogging
import no.nav.dagpenger.innsyn.settings.Configuration
import org.json.JSONObject
import java.time.LocalDate

class AktoerRegisterLookup(private val url: String = Configuration().application.aktoerregisteretUrl) {

    private val logger: KLogger = KotlinLogging.logger {}

    fun getGjeldendeAktoerIDFromIDToken(
        idToken: String,
        ident: String
    ): String {
        try {
            return getFirstMatchingAktoerIDFromIdent(ident, getAktoerResponse(idToken, ident, url).jsonObject)
        } catch (e: Exception) {
            logger.error("Could not successfully retrieve the aktoerID from aktoerregisteret's response", e)
        }
        return ""
    }

    private fun getFirstMatchingAktoerIDFromIdent(ident: String, jsonResponse: JSONObject): String {
        return (jsonResponse
                .getJSONObject(ident)
                .getJSONArray("identer")[0] as JSONObject)["ident"]
                .toString()
    }

    private fun getAktoerResponse(idToken: String, ident: String, url: String): Response {
        return khttp.get(
                url = url,
                headers = mapOf(
                        "Authorization" to "Bearer: $idToken",
                        "Nav-Call-Id" to "dagpenger-innsyn-api-${LocalDate.now().dayOfMonth}",
                        "Nav-Consumer-Id" to "dagpenger-innsyn-api",
                        "Nav-Personidenter" to ident
                )
        )
    }
}
