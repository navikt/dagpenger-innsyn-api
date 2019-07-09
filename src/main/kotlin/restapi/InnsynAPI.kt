package restapi

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.databind.SerializationFeature
import data.configuration.Configuration
import data.configuration.Profile
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.auth.parseAuthorizationHeader
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.dagpenger.events.Packet
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.slf4j.event.Level
import parsing.defaultParser
import parsing.getJSONParsed
import processing.convertInntektDataIntoProcessedRequest
import restapi.streams.Behov
import restapi.streams.InnsynProducer
import restapi.streams.InntektPond
import restapi.streams.KafkaInnsynProducer
import restapi.streams.KafkaInntektConsumer
import restapi.streams.producerConfig
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.*
import java.util.concurrent.locks.*
import kotlin.concurrent.withLock

private val logger: Logger = LogManager.getLogger()
private val config = Configuration()
private val authorizedUsers = listOf("localhost")
const val APPLICATION_NAME = "dp-inntekt-innsyn"
val filteredPackages: HashMap<String, Packet> = HashMap()


fun main() {
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    val kafkaConsumer = KafkaInntektConsumer(config, InntektPond()).also {
        it.start()
    }

    val kafkaProducer = KafkaInnsynProducer(producerConfig(
            APPLICATION_NAME,
            config.kafka.brokers))

    val app = embeddedServer(Netty, 8080) {
        innsynAPI(
                jwkProvider = jwkProvider,
                kafkaProducer = kafkaProducer
        )
    }.also {
        it.start(wait = false)
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })

    app.start(wait = false)

}

@Suppress("unused") // Referenced in application.conf
internal fun Application.innsynAPI(
        kafkaProducer: InnsynProducer,
        jwkProvider: JwkProvider
) {

    install(CORS) {
        method(HttpMethod.Options)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() //TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    if (config.application.profile == Profile.LOCAL) {
        logger.info("Not running with authentication, local build.")
    } else {
        logger.info("Running with authentication, not a local build")
        install(Authentication) {
            jwt {
                realm = "dagpenger-sommer"
                verifier(jwkProvider, config.application.jwksIssuer)
                authHeader { call ->
                    call.request.cookies["ID_token"]?.let {
                        HttpAuthHeader.Single("Bearer", it)
                    } ?: call.request.parseAuthorizationHeader()
                }
                validate { credentials ->
                    if (credentials.payload.subject in authorizedUsers) {
                        logger.info("authorization ok")
                        return@validate JWTPrincipal(credentials.payload)
                    } else {
                        logger.info("authorization failed")
                        return@validate null
                    }
                }
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        get("/inntekt") {
            logger.info("Attempting to retrieve token")
            val idToken = call.request.cookies["ID_token"]
            val beregningsdato = LocalDate.parse(call.request.cookies["beregningsdato"], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            if (idToken == null) {
                logger.error("Received invalid request without ID_token", call)
                call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
            } else if (!isValid(idToken)) {
                logger.error("Submitted ID_token is not valid", call)
                call.respond(HttpStatusCode.NotAcceptable, "Could not validate token")
            } else if (beregningsdato == null) {
                logger.error("Received invalid request without beregningsdato", call)
                call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
            } else if (!isValid(beregningsdato)) {
                logger.error("Submitted beregningsdato is not valid", call)
                call.respond(HttpStatusCode.NotAcceptable, "Could not validate token")
            } else {
                logger.info("Received valid ID_token, extracting actor and making requirement")
                val aktorID = getAktorIDFromIDToken(idToken)
                mapRequestToBehov(aktorID, beregningsdato).apply {
                    // TODO: Handle token
                    logger.info(this)
                    kafkaProducer.produceEvent(this)
                }.also {
                    val lock = ReentrantLock()
                    val notDone = lock.newCondition()
                    while (!filteredPackages.containsKey(it.behovId)) {
                        lock.withLock {
                            notDone.await()
                        }
                    }
                    // TODO: Respond with processed inntektData
                    print(filteredPackages[it.behovId]!!.toJson())
                    call.respond(HttpStatusCode.OK, defaultParser.toJsonString(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel"))))
                    notDone.signal()
                }
                logger.info("Received a request, responding with sample text for now")
                call.respond(HttpStatusCode.OK, defaultParser.toJsonString(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel"))))
            }
        }
    }
}

//TODO: Implement this
fun getAktorIDFromIDToken(idToken: String): String {
    return "123519375hkjsols90821"
}

//TODO: Implement this
fun isValid(token: String): Boolean {
    return true
}

//TODO: Implement this
fun isValid(beregningsDato: LocalDate): Boolean {
    return true
}

internal fun mapRequestToBehov(aktorId: String, beregningsDato: LocalDate): Behov = Behov(
        // TODO: Map personnummer to aktørId
        aktørId = aktorId,
        beregningsDato = beregningsDato
)
