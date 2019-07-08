package lookup

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()

fun getNameFromID(id: String): String {
    logger.info("Attempting to retrieve name from: https://data.brreg.no/enhetsregisteret/api/enheter/$id")
    val response = khttp.get("https://data.brreg.no/enhetsregisteret/api/enheter/$id")
    return if (response.statusCode != 200) {
        logger.error("Error retrieving organisation name")
        ("Ukjent")
    } else {
        response.jsonObject["navn"].toString()
    }
}