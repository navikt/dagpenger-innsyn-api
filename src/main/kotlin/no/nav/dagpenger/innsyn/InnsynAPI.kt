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
import no.nav.dagpenger.innsyn.restapi.streams.PacketStore
import no.nav.dagpenger.innsyn.restapi.streams.producerConfig
import no.nav.dagpenger.streams.KafkaCredential
import org.json.simple.JSONObject
import org.slf4j.event.Level
import java.net.URL
import java.time.LocalDate
import java.time.YearMonth
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val logger: KLogger = KotlinLogging.logger {}

private val config = Configuration()

val lock = ReentrantLock()
val condition = lock.newCondition()
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
    val jwkProvider = JwkProviderBuilder(URL(config.application.jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    val packetStore = HashMapPacketStore(condition)

    val kafkaConsumer = KafkaInntektConsumer(config, InntektPond(packetStore)).also {
        it.start()
    }

    val kafkaProducer = KafkaInnsynProducer(producerConfig(
            APPLICATION_NAME,
            config.kafka.brokers,
            KafkaCredential(config.kafka.user, config.kafka.password)))

    val app = embeddedServer(Netty, port = config.application.httpPort) {
        innsynAPI(
                packetStore = packetStore,
                kafkaProducer = kafkaProducer,
                jwkProvider = jwkProvider,
                healthChecks = listOf(kafkaConsumer as HealthCheck, kafkaProducer as HealthCheck)
        )
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        kafkaConsumer.stop()
        app.stop(10, 60, TimeUnit.SECONDS)
    })

    app.start(wait = false)
}

fun Application.innsynAPI(
    packetStore: PacketStore,
    kafkaProducer: InnsynProducer,
    jwkProvider: JwkProvider,
    healthChecks: List<HealthCheck>
) {

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
            authHeader {
                logger.info("Accessing auth cookie")
                val cookie = it.request.cookies["ID_token"]
                        ?: throw Exception("Cookie with name ID_Token not found")
                HttpAuthHeader.Single("Bearer", cookie)
            }
            validate {
                logger.info("Attempting to validate current request: $it")
                return@validate JWTPrincipal(it.payload)
            }
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
        authenticate("jwt") {
            get(config.application.applicationUrl) {
                logger.info("Attempting to retrieve token")
                val idToken = call.request.cookies["ID_token"]
                val beregningsdato = LocalDate.now()
                if (idToken == null) {
                    logger.error("Received invalid request without ID_token cookie", call)
                    call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
                } else {
                    logger.info("Received valid nav-esso_token, extracting actor and making requirement")
                    val aktorID = getAktorIDFromIDToken(idToken, getSubject())
//                mapRequestToBehov(aktorID, beregningsdato).apply {
//                    logger.info(this.toString())
//                    kafkaProducer.produceEvent(this)
//                }.also {
//                    while (!(packetStore.isDone(it.behovId))) {
//                        lock.withLock {
//                            condition.await(2000, TimeUnit.MILLISECONDS)
//                        }
//                    }
                    logger.info("Received a request, responding with sample text for now")
                    call.respond(HttpStatusCode.OK, defaultParser.toJsonString(testDataProcessedRequest))
                }
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
    logger.info(config.application.aktoerregisteretUrl)
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
        logger.info(response.toString())
        logger.info(response.jsonObject.toString())
        logger.info(response.jsonObject
                .getJSONObject(ident).toString())
        return (response.jsonObject
                .getJSONObject(ident)
                .getJSONArray("identer")[0] as org.json.JSONObject)["ident"]
                .toString()
    } catch (e: Exception) {
        logger.error("Something went wrong parsing the JSON response", e)
    }
    return ""
}

private fun PipelineContext<Unit, ApplicationCall>.getSubject(): String {
    return runCatching {
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
