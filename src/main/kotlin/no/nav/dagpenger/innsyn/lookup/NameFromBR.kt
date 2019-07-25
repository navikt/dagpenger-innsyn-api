package no.nav.dagpenger.innsyn.lookup

import no.nav.dagpenger.innsyn.data.configuration.Configuration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()
private val config = Configuration()

fun getNameFromID(id: String, url: String = config.application.enhetsregisteretUrl): String {
    val response = khttp.get(url + id)
    return if (response.statusCode != 200) {
        logger.error("Error retrieving organisation name $id")
        (id)
    } else {
        response.jsonObject["navn"].toString()
    }
}