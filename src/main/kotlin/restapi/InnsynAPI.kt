package restapi

import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.fasterxml.jackson.databind.SerializationFeature
import data.requests.APIPostRequest
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import parsing.LocalDate
import parsing.defaultParser
import parsing.getJSONParsed
import parsing.localDateParser
import processing.convertInntektDataIntoProcessedRequest
import restapi.streams.*
import java.time.DateTimeException
import java.util.concurrent.TimeUnit

val logger: Logger = LogManager.getLogger()
val APPLICATION_NAME = "dp-inntekt-innsyn"

fun main() {
    val kafkaConsumer = KafkaInntektConsumer(consumerConfig(APPLICATION_NAME, "localhost:9092"))
            .also {
        it.start()
    }

    val kafkaProducer = KafkaInnsynProducer(producerConfig(
            APPLICATION_NAME,
            "localhost:9092"))

    val app = embeddedServer(Netty, port = 8081) {
        api(kafkaProducer, kafkaConsumer)
    }.also {
        it.start(wait = false)
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })
}

internal fun Application.api(kafkaProducer: InnsynProducer, kafkaConsumer: KafkaInntektConsumer) {
    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        post("/inntekt") {
            val postRequest: APIPostRequest? = parsePOST(call)

            if (postRequest == null) {
                logger.info("PostRequest not successfully parsed. Terminating operation")
            } else if (!isValidPostRequest(postRequest)) {
                logger.info("PostRequest is not valid. Terminating operation")
                call.respond(HttpStatusCode.NotAcceptable, "Invalid JSON received")
            } else {
                logger.info("Received valid POST Request. Responding with sample text for now")
                call.respond(HttpStatusCode.OK, defaultParser.toJsonString(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel"))))

                mapRequestToBehov(postRequest).apply {
                    // TODO: Handle token
                    logger.info(this)
                    kafkaProducer.produceEvent(this)
                }.also {
                    print(kafkaConsumer.consume(it.behovId))
                    // TODO: Respond with consumed packet
                }
            }

        }
    }
}

fun isValidPostRequest(postRequest: APIPostRequest): Boolean {
    if (postRequest.beregningsdato > java.time.LocalDate.now()) {
        logger.info("Mottok beregningsdato i fremtiden")
    } else if (postRequest.beregningsdato < java.time.LocalDate.of(1970, 1, 1)) {
        logger.info("Mottok beregningsdato før epoch")
    } else if (postRequest.personnummer == "") {
        logger.info("Mottok tomt personnummer")
    } else if (postRequest.personnummer.length != 11) {
        logger.info("Mottok personnummer av ugyldig lengde")
    } else if (!Regex("[0-6][0-9][0-1][0-9]{8}").matches(postRequest.personnummer)) {
        logger.info("Mottok irregulert personnummer")
    } else if (!postRequest.token.equals("1234567890ABCDEFghijkl")) {
        logger.info("Mottok ugyldig token")
    } else {
        return true
    }
    return false
}

suspend fun parsePOST(call: ApplicationCall): APIPostRequest? {
    try {
        val payload: String = call.receiveText()
        return Klaxon()
                .fieldConverter(LocalDate::class, localDateParser)
                .parse<APIPostRequest>(payload)
    } catch (exception: KlaxonException) {
        logger.info("Received incomplete JSON through POST request: " + call.receiveText())
        call.respond(HttpStatusCode.NotAcceptable, "Invalid or incomplete JSON sent")
    } catch (exception: DateTimeException) {
        logger.info("Received incorrect Date format in JSON through POST request: " + call.receiveText())
        call.respond(HttpStatusCode.NotAcceptable, "Invalid Date Format in JSON")
    }
    return null
}

internal fun mapRequestToBehov(request: APIPostRequest): Behov = Behov(
        // TODO: Map personnummer to aktørId
        aktørId = request.personnummer,
        beregningsDato = request.beregningsdato
)

internal data class BehovRequest(
        val aktørId: String,
        val beregningsdato: String,
        val token: String
)

