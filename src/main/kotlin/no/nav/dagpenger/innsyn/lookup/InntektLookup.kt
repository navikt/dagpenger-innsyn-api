package no.nav.dagpenger.innsyn.lookup

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.getUserInformation
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt

fun getInntekt(
    behov: Behov,
    kafkaProducer: BehovProducer,
    packetStore: PacketStore,
    brønnøysundLookup: BrønnøysundLookup,
    timeout: Long = 30000
) : String {

    kafkaProducer.produceEvent(behov)
    runBlocking {
        withTimeout(timeout) {
            while (!(packetStore.isDone(behov.behovId))) {
                delay(500)
            }
        }
    }

    return moshiInstance.adapter(UserInformation::class.java).toJson(getUserInformation(testDataSpesifisertInntekt, brønnøysundLookup))
}
