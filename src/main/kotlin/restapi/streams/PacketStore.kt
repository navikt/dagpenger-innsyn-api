package restapi.streams

import no.nav.dagpenger.events.Packet

interface PacketStore {

    fun insert(packet: Packet)

    fun get(behovId: String): Packet

    fun isDone(behovId: String): Boolean

    class BehovNotFoundException(override val message: String) : RuntimeException(message)
}