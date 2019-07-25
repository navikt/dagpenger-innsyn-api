package no.nav.dagpenger.innsyn.lookup.objects

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.innsyn.settings.PacketKeys
import java.time.LocalDate

data class Behov(
    val behovId: String = ulidGenerator.nextULID(),
    val aktørId: String,
    val vedtakId: Int,
    val beregningsDato: LocalDate
) {
    companion object Mapper {
        private val ulidGenerator = ULID()

        fun toJson(behov: Behov): String = toJson(behov)

        fun fromJson(behov: Behov): String = fromJson(behov)

        fun toPacket(behov: Behov): Packet = Packet("{}").apply {
            this.putValue(PacketKeys.BEHOV_ID, behov.behovId)
            this.putValue(PacketKeys.AKTØR_ID, behov.aktørId)
            this.putValue(PacketKeys.BEREGNINGS_DATO, behov.beregningsDato)
        }
    }

    fun toPacket(): Packet = toPacket(this)
}