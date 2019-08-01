package no.nav.dagpenger.innsyn.lookup

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import mu.KLogger
import mu.KotlinLogging
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.getUserInformation
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt

private val logger: KLogger = KotlinLogging.logger {}

fun getInntektResponse(
    behov: Behov,
    kafkaProducer: BehovProducer,
    packetStore: PacketStore,
    brønnøysundLookup: BrønnøysundLookup
): Response {

    try {
        kafkaProducer.produceEvent(behov)
        runBlocking {
            withTimeout(30000) {
                while (!(packetStore.isDone(behov.behovId))) {
                    delay(500)
                }
            }
        }
    } catch (e: TimeoutCancellationException) {
        logger.error("Timed out waiting for kafka", e)
        return Response(HttpStatusCode.GatewayTimeout, moshiInstance.adapter(UserInformation::class.java).toJson(getUserInformation(testDataSpesifisertInntekt, brønnøysundLookup)))
    }

    return Response(HttpStatusCode.OK, moshiInstance.adapter(UserInformation::class.java).toJson(getUserInformation(testDataSpesifisertInntekt, brønnøysundLookup)))
}

data class Response(val statusCode: HttpStatusCode, val message: Any)