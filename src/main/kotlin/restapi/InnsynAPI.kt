package restapi

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restapi.streams.*
import restapi.streams.InnsynProducer
import restapi.streams.KafkaInnsynProducer
import restapi.streams.KafkaInntektConsumer
import restapi.streams.producerConfig
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

    val app = embeddedServer(Netty, port = 8092) {
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
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
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
            mapRequestToBehov(call.receive()).apply {
                logger.info("Received new request from somewhere")
                // TODO: Log the whole request
                // TODO: Handle token
                logger.info(this)
                kafkaProducer.produceEvent(this)
            }.also {
                kafkaConsumer.consume(it.aktørId)
                call.respond(getExample())
            }
        }
    }
}

internal fun mapRequestToBehov(request: BehovRequest): Behov = Behov(
        aktørId = request.aktørId,
        beregningsDato = request.beregningsdato
)

internal data class BehovRequest(
        val aktørId: String,
        val beregningsdato: String,
        val token: String
)


