package no.nav.dagpenger.innsyn.lookup

import mu.KotlinLogging
import no.nav.dagpenger.innsyn.settings.Configuration

private val logger = KotlinLogging.logger { }

class BrønnøysundLookup(private val url: String = Configuration().application.enhetsregisteretUrl) {
    private val cache: HashMap<String, String> = HashMap()

    fun getNameFromBroennoeysundRegisterByID(id: String): String {
        if (cache.containsKey(id)) {
            logger.debug("Using cache for $id : ${cache.get(id)}")
            return cache.get(id)!!
        }
        val response = khttp.get(url + id)
        return if (response.statusCode != 200) {
            logger.error("Error retrieving organisation name $id")
            id
        } else {
            logger.debug("Successfully retrieved name for $id from BR")
            cache.set(id, response.jsonObject["navn"].toString())
            response.jsonObject["navn"].toString()
        }
    }
}