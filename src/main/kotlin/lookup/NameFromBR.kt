package lookup

import data.configuration.Configuration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()
private val config = Configuration()

fun getNameFromID(id: String): String {
    logger.debug("Attempting to retrieve name from: ${config.application.enhetsregisteretUrl}$id")
    val response = khttp.get(config.application.enhetsregisteretUrl + id)
    return if (response.statusCode != 200) {
        logger.error("Error retrieving organisation name $id")
        (id)
    } else {
        response.jsonObject["navn"].toString()
    }
}