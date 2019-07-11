package restapi.streams

import no.nav.dagpenger.events.Packet

internal class HashMapPacketStore : PacketStore{
    private val filteredPackets: HashMap<String, Packet> = HashMap()

    override fun insert(packet: Packet){
        filteredPackets[packet.getStringValue(PacketKeys.BEHOV_ID)] = packet
    }

    override fun get(behovId: String): Packet? = filteredPackets[behovId]

    override fun isDone(behovId: String): Boolean = filteredPackets.containsKey(behovId)
}