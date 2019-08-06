package no.nav.dagpenger.innsyn.lookup

import khttp.responses.Response
import no.nav.dagpenger.innsyn.settings.Configuration
import org.json.JSONArray
import org.json.JSONObject
import java.lang.RuntimeException
import java.time.LocalDate

class AktørregisterLookup(private val url: String = Configuration().application.aktoerregisteretUrl) {

    fun getGjeldendeAktørIDFromIDToken(
        idToken: String,
        ident: String
    ): String {
        return getFirst(getIdenter(idToken, ident, url))
    }

    private fun getFirst(identer: JSONArray): String {
        return (identer[0] as JSONObject)["ident"]
                .toString()
    }

    private fun getAktørResponse(idToken: String, ident: String, url: String): Response {
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

    private fun getIdenter(idToken: String, ident: String, url: String): JSONArray {
        val jsonObject = getAktørResponse(idToken, ident, url)
                .jsonObject
                .getJSONObject(ident)

        val identer = jsonObject
                .getJSONArray("identer")

        if (identer.length() == 0) {
            throw AktørIdNotFoundException("Did not receive a matching aktør from register")
        }
        return identer
    }
}

class AktørIdNotFoundException(override val message: String) : RuntimeException(message)
