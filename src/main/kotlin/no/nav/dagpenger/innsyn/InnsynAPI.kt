package no.nav.dagpenger.innsyn

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.exceptions.JWTDecodeException
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.TimeoutCancellationException
import mu.KLogger
import mu.KotlinLogging
import no.nav.dagpenger.events.Problem
import no.nav.dagpenger.innsyn.lookup.AktørIdNotFoundException
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.lookup.InntektLookup
import no.nav.dagpenger.innsyn.lookup.InntektPond
import no.nav.dagpenger.innsyn.lookup.KafkaBehovProducer
import no.nav.dagpenger.innsyn.lookup.KafkaInntektConsumer
import no.nav.dagpenger.innsyn.lookup.objects.HashMapPacketStore
import no.nav.dagpenger.innsyn.lookup.producerConfig
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.routing.inntekt
import no.nav.dagpenger.innsyn.routing.naischecks
import no.nav.dagpenger.innsyn.settings.Configuration
import no.nav.dagpenger.streams.KafkaCredential
import org.slf4j.event.Level
import java.net.URL
import java.util.concurrent.TimeUnit

private val logger: KLogger = KotlinLogging.logger {}

private val config = Configuration()

const val APPLICATION_NAME = "dp-inntekt-innsyn-api"

fun main() {

    logger.debug("Creating jwkProvider for ${config.application.jwksUrl}")
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    val packetStore = HashMapPacketStore()

    val kafkaConsumer = KafkaInntektConsumer(config, InntektPond(packetStore)).also {
        it.start()
    }

    val kafkaProducer = KafkaBehovProducer(producerConfig(
            APPLICATION_NAME,
            config.kafka.brokers,
            KafkaCredential(config.kafka.user, config.kafka.password)
    ))

    val brLookup = BrønnøysundLookup(config.application.enhetsregisteretUrl)

    val inntektLookup = InntektLookup(kafkaProducer, packetStore, brLookup)

    val aktørregisterLookup = AktørregisterLookup(config.application.aktoerregisteretUrl)

    logger.debug("Creating application with port ${config.application.httpPort}")
    val app = embeddedServer(Netty, port = config.application.httpPort) {
        innsynAPI(
                jwkProvider = jwkProvider,
                healthChecks = listOf(kafkaConsumer as HealthCheck, kafkaProducer as HealthCheck),
                aktørregisterLookup = aktørregisterLookup,
                inntektLookup = inntektLookup
        )
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })

    logger.debug("Starting application")
    app.start(wait = false)
}

fun Application.innsynAPI(
    jwkProvider: JwkProvider,
    healthChecks: List<HealthCheck>,
    aktørregisterLookup: AktørregisterLookup,
    inntektLookup: InntektLookup
) {

    logger.debug("Installing jackson for content negotiation")
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    logger.debug("Installing authentication with jwksIssuer: ${config.application.jwksIssuer}")
    install(Authentication) {
        jwt(name = "jwt") {
            realm = "dp-inntekt-api"
            verifier(jwkProvider, config.application.jwksIssuer) {
                acceptNotBefore(10)
                acceptIssuedAt(10)
            }
            authHeader {
                val cookie = it.request.cookies["ID_token"]
                        ?: throw CookieNotSetException("Cookie with name ID_Token not found")
                HttpAuthHeader.Single("Bearer", cookie)
            }
            validate {
                return@validate JWTPrincipal(it.payload)
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            logger.error("Request failed!", cause)
            val error = Problem(
                    title = "Uhåndtert feil!"
            )
            call.respond(HttpStatusCode.InternalServerError, error)
        }
        exception<CookieNotSetException> { cause ->
            logger.warn("Unauthorized call", cause)
            val statusCode = HttpStatusCode.Unauthorized
            val error = Problem(
                    title = "Ikke innlogget",
                    detail = "${cause.message}",
                    status = statusCode.value
            )
            call.respond(statusCode, error)
        }
        exception<JWTDecodeException> { cause ->
            logger.warn("JWT decoding failed", cause)
            val statusCode = HttpStatusCode.Unauthorized
            val error = Problem(
                    title = "Klarte ikke hente fødselsnummer",
                    detail = "${cause.message}",
                    status = statusCode.value
            )
            call.respond(statusCode, error)
        }
        exception<TimeoutCancellationException> { cause ->
            logger.error("Timed out waiting for Kafka", cause)
            val statusCode = HttpStatusCode.GatewayTimeout
            val error = Problem(
                    title = "Klarte ikke hente inntekt. Prøv igjen senere",
                    detail = "${cause.message}",
                    status = statusCode.value
            )
            call.respond(statusCode, error)
        }
        exception<AktørIdNotFoundException> {cause ->
            logger.error("Could not retrieve aktørId from Aktørregisteret", cause)
            val statusCode = HttpStatusCode.InternalServerError
            val error = Problem(
                    title = "Klarte ikke hente Aktør ID",
                    detail = "${cause.message}",
                    status = statusCode.value
            )
            call.respond(statusCode, error)
        }
    }

    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            !call.request.path().startsWith("/isAlive") &&
                    !call.request.path().startsWith("/isReady") &&
                    !call.request.path().startsWith("/metrics")
        }
    }

    routing {
        inntekt(aktørregisterLookup, inntektLookup)
        naischecks(healthChecks)
    }
}
