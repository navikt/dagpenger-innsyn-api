package no.nav.dagpenger.innsyn.lookup

import mu.KotlinLogging
import no.nav.dagpenger.innsyn.settings.Configuration

private val logger = KotlinLogging.logger { }
private val cache: HashMap<String, String> = HashMap()

fun getNameFromBrønnøysundRegisterByID(id: String, url: String = Configuration().application.enhetsregisteretUrl): String {
    if (cache.containsKey(id)) {
        logger.debug("Using cache for $id : ${cache.get(id)}")
        return cache.get(id)!!
    }
    val response = khttp.get(url + id)
    if (response.statusCode != 200) {
        logger.error("Error retrieving organisation name $id")
        return id
    } else {
        logger.debug("Successfully retrieved name for $id from BR")
        cache.set(id, response.jsonObject["navn"].toString())
        return response.jsonObject["navn"].toString()
    }
}