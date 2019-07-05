package restapi.streams

import no.nav.dagpenger.events.Packet
import de.huxhorn.sulky.ulid.ULID

data class Behov(
        val behovId: String = ulidGenerator.nextULID(),
        val aktørId: String,
        // TODO: Change to LocalDate
        val beregningsDato: String
){
    companion object Mapper {
        private val ulidGenerator = ULID()

        fun toPacket(behov: Behov): Packet = Packet("{}").apply {
            this.putValue(PacketKeys.AKTØR_ID, behov.aktørId)
            this.putValue(PacketKeys.BEREGNINGS_DATO, behov.beregningsDato)}
        }
    fun toPacket(): Packet = Mapper.toPacket(this)
}