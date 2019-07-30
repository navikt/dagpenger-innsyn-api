package no.nav.dagpenger.innsyn.lookup.objects

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.settings.PacketKeys
import java.time.LocalDate

data class Behov(
    val behovId: String = ulidGenerator.nextULID(),
    val aktørId: String,
    val vedtakId: Int = 1,
    val beregningsDato: LocalDate
) {
    companion object Mapper {
        private val ulidGenerator = ULID()

        private val adapter = moshiInstance.adapter<Behov>(Behov::class.java)

        fun toJson(behov: Behov): String = adapter.toJson(behov)

        fun fromJson(json: String): Behov? = adapter.fromJson(json)

        fun toPacket(behov: Behov): Packet = Packet("{}").apply {
            this.putValue(PacketKeys.BEHOV_ID, behov.behovId)
            this.putValue(PacketKeys.AKTØR_ID, behov.aktørId)
            this.putValue(PacketKeys.VEDTAK_ID, behov.vedtakId)
            this.putValue(PacketKeys.BEREGNINGS_DATO, behov.beregningsDato)
        }
    }

    fun toPacket(): Packet = Mapper.toPacket(this)
}