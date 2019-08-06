package no.nav.dagpenger.innsyn.lookup

import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.KvalifisertResultat
import no.nav.dagpenger.innsyn.conversion.PeriodeResultat
import no.nav.dagpenger.innsyn.conversion.SatsResultat
import no.nav.dagpenger.innsyn.conversion.getUserInformation
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.lookup.objects.Behov
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.PacketKeys

private val logger = KotlinLogging.logger { }

val spesifisertInntektJsonAdapter: JsonAdapter<SpesifisertInntekt> = moshiInstance.adapter(SpesifisertInntekt::class.java)
val spesifisertPeriodeJsonAdapter: JsonAdapter<PeriodeResultat> = moshiInstance.adapter(PeriodeResultat::class.java)
val spesifisertSatsJsonAdapter: JsonAdapter<SatsResultat> = moshiInstance.adapter(SatsResultat::class.java)
val spesifisertResultatJsonAdapter: JsonAdapter<KvalifisertResultat> = moshiInstance.adapter(KvalifisertResultat::class.java)

class InntektLookup(
    private val kafkaProducer: BehovProducer,
    private val packetStore: PacketStore,
    private val brønnøysundLookup: BrønnøysundLookup
) {
    fun getInntekt(
        behov: Behov,
        timeout: Long = 30000
    ): String {

        kafkaProducer.produceEvent(behov)
        logger.info("Kafka produced behov: $behov")
        runBlocking {
            withTimeout(timeout) {
                while (!(packetStore.isDone(behov.behovId))) {
                    delay(500)
                }
            }
        }

        val packet = packetStore.get(behov.behovId)
        val spesifisertInntekt = packet.getObjectValue(PacketKeys.SPESIFISERT_INNTEKT) { serialized ->
            checkNotNull(
                    spesifisertInntektJsonAdapter.fromJsonValue(serialized)
            )
        }
        val periodeResultat = packet.getObjectValue(PacketKeys.PERIODE_RESULTAT) { serialized ->
            checkNotNull(
                    spesifisertPeriodeJsonAdapter.fromJsonValue(serialized)
            )
        }
        val satsResultat = packet.getObjectValue(PacketKeys.SATS_RESULTAT) { serialized ->
            checkNotNull(
                    spesifisertSatsJsonAdapter.fromJsonValue(serialized)
            )
        }

        val kvalifisertResultat = packet.getObjectValue(PacketKeys.MINSTEINNTEKT_RESULTAT) { serialized ->
            checkNotNull(
                    spesifisertResultatJsonAdapter.fromJsonValue(serialized)
            )
        }

        return moshiInstance.adapter(UserInformation::class.java).toJson(getUserInformation(spesifisertInntekt, brønnøysundLookup, periodeResultat, satsResultat, kvalifisertResultat))
    }
}
