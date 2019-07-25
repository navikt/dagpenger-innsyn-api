package no.nav.dagpenger.innsyn.lookup.objects

import no.nav.dagpenger.events.Packet

internal interface PacketStore {

    fun insert(packet: Packet)

    fun get(behovId: String): Packet?

    fun isDone(behovId: String): Boolean
}