package no.nav.dagpenger.innsyn.routing

import com.auth0.jwt.exceptions.JWTDecodeException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.util.pipeline.PipelineContext
import mu.KLogger
import mu.KotlinLogging
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.BehovProducer
import no.nav.dagpenger.innsyn.lookup.getInntektResponse
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.Configuration
import java.time.LocalDate

private val logger: KLogger = KotlinLogging.logger {}
private val config = Configuration()

internal fun Routing.inntekt(
        packetStore: PacketStore,
        kafkaProducer: BehovProducer,
        aktørregisterLookup: AktørregisterLookup,
        brønnøysundLookup: BrønnøysundLookup
) {
    authenticate("jwt") {
        get(config.application.applicationUrl) {
            val idToken = call.request.cookies["ID_token"]
            if (idToken == null) {
                logger.error("Received invalid request without ID_token cookie", call)
                call.respond(HttpStatusCode.NotAcceptable, "Missing required cookies")
            } else {
                val aktørId = aktørregisterLookup.getGjeldendeAktørIDFromIDToken(idToken, getSubject())
                val behov = mapRequestToBehov(aktørId, LocalDate.now())

                val (statusCode, response) = getInntektResponse(behov, kafkaProducer, packetStore, brønnøysundLookup)
                call.respond(statusCode, response)
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getSubject(): String {
    return runCatching {
        call.authentication.principal?.let {
            (it as JWTPrincipal).payload.subject
        } ?: throw JWTDecodeException("Unable to get subject from JWT")
    }.getOrElse {
        logger.error(it) { "Unable to get subject from authentication" }
        return@getOrElse "UNKNOWN"
    }
}

internal fun mapRequestToBehov(aktorId: String, beregningsDato: LocalDate): Behov = Behov(
        aktørId = aktorId,
        beregningsDato = beregningsDato
)