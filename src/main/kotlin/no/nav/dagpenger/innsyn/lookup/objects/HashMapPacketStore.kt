package no.nav.dagpenger.innsyn.lookup.objects

import no.nav.dagpenger.events.Packet
import java.util.concurrent.locks.Condition
import no.nav.dagpenger.innsyn.settings.PacketKeys

internal class HashMapPacketStore(private val condition: Condition) : PacketStore {
    private val filteredPackets: HashMap<String, Packet> = HashMap()

    override fun insert(packet: Packet) {
        filteredPackets[packet.getStringValue(PacketKeys.BEHOV_ID)] = packet
        condition.signalAll()
    }

    override fun get(behovId: String): Packet = filteredPackets[behovId]
            ?: throw PacketStore.BehovNotFoundException("BehovId: $behovId")

    override fun isDone(behovId: String): Boolean = filteredPackets.containsKey(behovId)
}