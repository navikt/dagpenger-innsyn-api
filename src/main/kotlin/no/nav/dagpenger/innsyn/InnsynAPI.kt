package no.nav.dagpenger.innsyn

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.exceptions.JWTDecodeException
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.DefaultExports
import mu.KLogger
import mu.KotlinLogging
import no.nav.dagpenger.innsyn.data.configuration.Configuration
import no.nav.dagpenger.innsyn.data.configuration.Profile
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.monitoring.HealthStatus
import no.nav.dagpenger.innsyn.parsing.defaultParser
import no.nav.dagpenger.innsyn.parsing.getJSONParsed
import no.nav.dagpenger.innsyn.processing.convertInntektDataIntoProcessedRequest
import no.nav.dagpenger.innsyn.restapi.streams.Behov
import no.nav.dagpenger.innsyn.restapi.streams.HashMapPacketStore
import no.nav.dagpenger.innsyn.restapi.streams.InnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.InntektPond
import no.nav.dagpenger.innsyn.restapi.streams.KafkaInnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.KafkaInntektConsumer
import no.nav.dagpenger.innsyn.restapi.streams.producerConfig
import no.nav.dagpenger.streams.KafkaCredential
import org.json.simple.JSONObject
import org.slf4j.event.Level
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.naming.ConfigurationException

private val logger: KLogger = KotlinLogging.logger {}

private val config = Configuration()
val APPLICATION_NAME = "dp-inntekt-innsyn"

fun main() {
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    val packetStore = HashMapPacketStore()

    val kafkaConsumer = KafkaInntektConsumer(config, InntektPond(packetStore)).also {
        it.start()
    }

    val kafkaProducer = KafkaInnsynProducer(producerConfig(
            APPLICATION_NAME,
            config.kafka.brokers,
            KafkaCredential(config.kafka.user, config.kafka.password)))

    val app = embeddedServer(Netty, port = config.application.httpPort) {
        innsynAPI(
                kafkaProducer,
                jwkProvider = jwkProvider,
                healthChecks = listOf(kafkaConsumer as HealthCheck, kafkaProducer as HealthCheck)
        )
    }

    if (config.application.profile == Profile.LOCAL) {
        embeddedServer(Netty, port = 9011) {
            aktoerRegisterMock()
        }.start(wait = false)
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })

    app.start(wait = false)
}

fun Application.aktoerRegisterMock() {
    if (config.application.profile != Profile.LOCAL) {
        throw ConfigurationException("This is the wrong config for this service")
    }
    routing {
        get(config.application.aktoerregisteretUrl) {
            if (call.request.headers["Authorization"] == null || call.request.headers["Nav-Call-Id"] == null || call.request.headers["Nav-Consumer-Id"] == null || call.request.headers["Nav-Personidenter"] == null) {
                logger.info("Lacking Cookie Received")
                call.respond(HttpStatusCode.NotAcceptable, "Lacking Headers")
            } else {
                logger.info("Received set headers. No authentication performed. Responding with default")
                val defaultResponse =
                        """
                            {
                                "${call.request.headers["Authorization"]}": {
                                    "identer": [
                                        {
                                        "ident": "1000100625562",
                                        "identgruppe": "AktoerId",
                                        "gjeldende": false
                                        }
                                    ]
                                }
                            }
                        """
                call.respond(HttpStatusCode.OK, defaultResponse)
            }
        }
    }
}

fun Application.innsynAPI(
    kafkaProducer: InnsynProducer,
    jwkProvider: JwkProvider,
    healthChecks: List<HealthCheck>
) {

    install(CORS) {
        method(HttpMethod.Options)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Authentication) {
        jwt(name = "jwt") {
            realm = "dp-inntekt-api"
            verifier(jwkProvider, config.application.jwksIssuer) {
                acceptNotBefore(10)
                acceptIssuedAt(10)
            }
            authHeader { call ->
                val cookie = call.request.cookies["ID_token"]
                        ?: throw Exception("Cookie with name ID_Token not found")
                HttpAuthHeader.Single("Bearer", cookie)
            }
            validate { credentials ->
                return@validate JWTPrincipal(credentials.payload)
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        get(config.application.applicationUrl) {
            logger.info("Attempting to retrieve token")
            val idToken = call.request.cookies["nav-esso"]
            val beregningsdato: LocalDate? = try {
                LocalDate.parse(call.request.cookies["beregningsdato"], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (e: NullPointerException) {
                null
            }
            if (idToken == null) {
                logger.error("Received invalid request without nav-esso token", call)
                call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
            } else if (beregningsdato == null) {
                logger.error("Received invalid request without beregningsdato", call)
                call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
            } else if (!isValid(beregningsdato)) {
                logger.error("Submitted beregningsdato is not valid", call)
                call.respond(HttpStatusCode.NotAcceptable, "Could not validate token")
            } else {
                logger.info("Received valid nav-esso_token, extracting actor and making requirement")
                val aktorID = getAktorIDFromIDToken(idToken, getSubject())
                mapRequestToBehov(aktorID, beregningsdato).apply {
                    logger.info(this.toString())
                    kafkaProducer.produceEvent(this)
                }
                logger.info("Received a request, responding with sample text for now")
                call.respond(HttpStatusCode.OK, defaultParser.toJsonString(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel"))))
            }
        }

        get("/isAlive") {
            if (healthChecks.all { it.status() == HealthStatus.UP }) call.respond(HttpStatusCode.OK, "OK")
            else call.response.status(HttpStatusCode.ServiceUnavailable)
        }

        get("/isReady") {
            call.respond(HttpStatusCode.OK, "OK")
        }

        get("/metrics") {
            val collectorRegistry = CollectorRegistry.defaultRegistry
            DefaultExports.initialize()

            val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
            call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
                TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
            }
        }
    }
}

fun getAktorIDFromIDToken(idToken: String, ident: String): String {
    val response = khttp.get(
            url = config.application.aktoerregisteretUrl,
            headers = mapOf(
                    "Authorization" to idToken,
                    "Nav-Call-Id" to "dagpenger-innsyn-api-${LocalDate.now().dayOfMonth}",
                    "Nav-Consumer-Id" to "dagpenger-innsyn-api",
                    "Nav-Personidenter" to ident
            )
    )
    try {
        return (response.jsonObject.getJSONObject(ident).getJSONArray("identer")[0] as JSONObject).get("ident").toString()
    } catch (e: Exception) {
        logger.error("Something when wrong parsing the JSON response", e)
    }
    return ""
}

fun isValid(beregningsDato: LocalDate): Boolean {
    return beregningsDato.isAfter(LocalDate.now().minusMonths(2))
}

private fun PipelineContext<Unit, ApplicationCall>.getSubject(): String {
    return runCatching {
        logger.info(call.toString())
        logger.info(call.authentication.toString())
        logger.info(call.authentication.principal.toString())
        logger.info((call.authentication.principal!! as JWTPrincipal).payload.toString())
        logger.info((call.authentication.principal!! as JWTPrincipal).payload.subject)
        call.authentication.principal?.let {
            (it as JWTPrincipal).payload.subject
        } ?: throw JWTDecodeException("Unable to get subject from JWT")
    }.getOrElse {
        logger.error(it) { "Unable to get subject" }
        return@getOrElse "UNKNOWN"
    }
}

internal fun mapRequestToBehov(aktorId: String, beregningsDato: LocalDate): Behov = Behov(
        aktørId = aktorId,
        beregningsDato = beregningsDato
)
