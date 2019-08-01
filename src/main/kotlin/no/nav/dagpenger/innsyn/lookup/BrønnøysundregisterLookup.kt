package no.nav.dagpenger.innsyn.lookup

import mu.KotlinLogging
import no.nav.dagpenger.innsyn.settings.Configuration

private val logger = KotlinLogging.logger { }

class BrønnøysundLookup(private val url: String = Configuration().application.enhetsregisteretUrl) {
    private val cache: HashMap<String, String> = HashMap()

    fun getNameFromBrønnøysundRegisterByID(id: String): String {
        logger.info("Attempting to retrieve br from: $url for $id")
        if (cache.containsKey(id)) {
            logger.debug("Using cache for $id : ${cache[id]}")
            return cache[id]!!
        }
        val response = khttp.get(url + id)
        return if (response.statusCode != 200) {
            logger.error("Error retrieving organisation name $id")
            id
        } else {
            logger.debug("Successfully retrieved name for $id from BR")
            cache[id] = response.jsonObject["navn"].toString()
            response.jsonObject["navn"].toString()
        }
    }
}