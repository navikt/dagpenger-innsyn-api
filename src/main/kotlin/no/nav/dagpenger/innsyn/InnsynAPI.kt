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
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.jackson.jackson
import io.ktor.request.path
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
import no.nav.dagpenger.innsyn.data.inntekt.Employer
import no.nav.dagpenger.innsyn.data.inntekt.EmployerSummary
import no.nav.dagpenger.innsyn.data.inntekt.EmploymentPeriode
import no.nav.dagpenger.innsyn.data.inntekt.Income
import no.nav.dagpenger.innsyn.data.inntekt.MonthIncomeInformation
import no.nav.dagpenger.innsyn.data.inntekt.ProcessedRequest
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.monitoring.HealthStatus
import no.nav.dagpenger.innsyn.parsing.defaultParser
import no.nav.dagpenger.innsyn.restapi.streams.Behov
import no.nav.dagpenger.innsyn.restapi.streams.HashMapPacketStore
import no.nav.dagpenger.innsyn.restapi.streams.InnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.InntektPond
import no.nav.dagpenger.innsyn.restapi.streams.KafkaInnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.KafkaInntektConsumer
import no.nav.dagpenger.innsyn.restapi.streams.producerConfig
import no.nav.dagpenger.streams.KafkaCredential
import org.slf4j.event.Level
import java.net.URL
import java.time.LocalDate
import java.time.YearMonth
import java.util.concurrent.TimeUnit

private val logger: KLogger = KotlinLogging.logger {}

private val config = Configuration()
val APPLICATION_NAME = "dp-inntekt-innsyn"

private val testDataIncome = Income(
        income = 155.13,
        verdikode = "Total Lønnsinntekt"
)

private val testDataEmployer = Employer(
        name = "NAV",
        orgID = "114235",
        incomes = listOf(testDataIncome)
)

private val testDataMonthIncomeInformation = MonthIncomeInformation(
        month = YearMonth.of(2019, 1),
        employers = listOf(testDataEmployer)
)

private val testDataEmployerSummary = EmployerSummary(
        name = "NAV",
        orgID = "114235",
        income = 155.13,
        employmentPeriodes = listOf(EmploymentPeriode(
                startDateYearMonth = YearMonth.of(2019, 1),
                endDateYearMonth = YearMonth.of(2019, 3)
        )
        )
)

private val testDataProcessedRequest = ProcessedRequest(
        personnummer = "131165542135",
        totalIncome36 = 155.13,
        totalIncome12 = 80.25,
        employerSummaries = listOf(testDataEmployerSummary),
        monthsIncomeInformation = listOf(testDataMonthIncomeInformation)
)

fun main() {

    logger.debug("Creating jwkProvider for ${config.application.jwksUrl}")
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    logger.debug("Creating hashMapPacketStore")
    val packetStore = HashMapPacketStore()

    logger.debug("Creating kafkaConsumer for application and starting it")
    val kafkaConsumer = KafkaInntektConsumer(config, InntektPond(packetStore)).also {
        it.start()
    }

    logger.debug("Creating kafkaProducer for application")
    val kafkaProducer = KafkaInnsynProducer(producerConfig(
            APPLICATION_NAME,
            config.kafka.brokers,
            KafkaCredential(config.kafka.user, config.kafka.password)
    ))

    logger.debug("Creating application with port ${config.application.httpPort}")
    val app = embeddedServer(Netty, port = config.application.httpPort) {
        innsynAPI(
                kafkaProducer,
                jwkProvider = jwkProvider,
                healthChecks = listOf(kafkaConsumer as HealthCheck, kafkaProducer as HealthCheck)
        )
    }

    logger.debug("Adding shutdownhook to kafkaConsumer")
    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })

    logger.debug("Starting application without wait")
    app.start(wait = false)
}

fun Application.innsynAPI(
    kafkaProducer: InnsynProducer,
    jwkProvider: JwkProvider,
    healthChecks: List<HealthCheck>
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
                logger.debug("Accessing ID_token for authenticating user")
                val cookie = it.request.cookies["ID_token"]
                        ?: throw Exception("Cookie with name ID_Token not found")
                HttpAuthHeader.Single("Bearer", cookie)
            }
            validate {
                logger.debug("Attempting to validate user ID_token")
                return@validate JWTPrincipal(it.payload)
            }
        }
    }

    logger.debug("Installing call logging")
    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            !call.request.path().startsWith("/isAlive") &&
                    !call.request.path().startsWith("/isReady") &&
                    !call.request.path().startsWith("/metrics")
        }
    }

    logger
    routing {
        authenticate("jwt") {
            get(config.application.applicationUrl) {
                logger.debug("Call to ${config.application.applicationUrl} received")

                logger.debug("Attempting to retrieve ID_token from cookies from caller")
                val idToken = call.request.cookies["ID_token"]

                logger.debug("Setting beregningsdato to current date")
                val beregningsdato = LocalDate.now()

                logger.debug("Checking that ID_token is present: Should not be possible to get this far without ID_token")
                if (idToken == null) {
                    logger.error("Received invalid request without ID_token cookie", call)

                    logger.debug("Could not find ID_token, implying either authentication or cookie retrieval failed")
                    logger.debug("Responding with 406 NotAcceptable: Missing required cookies")
                    call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
                } else {
                    logger.debug("Received valid ID_token, will attempt to extract actor and make requirement")

                    logger.debug("Attempting to extract aktoerID from ID_token (OIDC) and subject")
                    val aktoerID = getAktoerIDFromIDToken(idToken, getSubject())

                    logger.debug("Attempting to create behov for aktoerID: $aktoerID")
                    mapRequestToBehov(aktoerID, beregningsdato).apply {
                        logger.debug("Created behov: $this")
                        kafkaProducer.produceEvent(this)
                    }
                    logger.debug("Completed request. Responding with 200 OK and example result")
                    call.respond(HttpStatusCode.OK, defaultParser.toJsonString(testDataProcessedRequest))

                    logger.debug("Finished handling ${config.application.applicationUrl} call")
                }
            }
        }

        get("/isAlive") {
            logger.debug("Call to isAlive received")

            logger.debug("Checking health status and responding with either 200 OK or 503 Service Unavailable")
            if (healthChecks.all { it.status() == HealthStatus.UP }) {
                logger.debug("Health status is up, responding with 200 OK")
                call.respond(HttpStatusCode.OK, "OK")
            }
            else {
                logger.debug("Health status is not up, responding with 503 Service Unavailable")
                call.response.status(HttpStatusCode.ServiceUnavailable)
            }

            logger.debug("Finished handling isAlive call")
        }

        get("/isReady") {
            logger.debug("Call to isReady received")

            logger.debug("Attempting to respond 200 OKto isReady call")
            call.respond(HttpStatusCode.OK, "OK")

            logger.debug("Finished handling isReady call")
        }

        get("/metrics") {
            logger.debug("Call to metrics received")

            logger.debug("Attempting to access CollectorRegistry")
            val collectorRegistry = CollectorRegistry.defaultRegistry
            DefaultExports.initialize()

            logger.debug("Attempting to acquire names asked for from collectorRegistry")
            val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()

            logger.debug("Attempting to make a textWriter response for bundle of names: $names")
            call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
                TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
            }

            logger.debug("Finished handling metrics call")
        }
    }
}

fun getAktoerIDFromIDToken(idToken: String, ident: String): String {
    logger.debug("Sending request to aktoerregisteret to retrieve aktoerID for subject")
    logger.debug("Current url for aktoerregisteret: $config.application.aktoerregisteretUrl")

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
        logger.debug("Attempting to retrieve subject aktoerID from aktoerregisteret's response")

        return (response.jsonObject
                .getJSONObject(ident)
                .getJSONArray("identer")[0] as org.json.JSONObject)["ident"]
                .toString()
    } catch (e: Exception) {
        logger.error("Could not successfully retrieve the aktoerID from aktoerregisteret's response", e)
    }
    logger.debug("Did not successfully retrieve the aktoerID and responding with empty string")
    return ""
}

private fun PipelineContext<Unit, ApplicationCall>.getSubject(): String {
    logger.debug("Attempting to retrieve subject from authentication token")
    return runCatching {
        call.authentication.principal?.let {
            (it as JWTPrincipal).payload.subject
        } ?: throw JWTDecodeException("Unable to get subject from JWT")
    }.getOrElse {
        logger.error(it) { "Unable to get subject from authentication" }
        logger.debug("Could not retrieve subject from JWT, responding with UNKNOWN")
        return@getOrElse "UNKNOWN"
    }
}

internal fun mapRequestToBehov(aktorId: String, beregningsDato: LocalDate): Behov = Behov(
        aktørId = aktorId,
        beregningsDato = beregningsDato
)
