package no.nav.dagpenger.innsyn.lookup

import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyAll
import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.PacketKeys
import no.nav.dagpenger.innsyn.settings.behovId
import no.nav.dagpenger.streams.Topics
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.junit.jupiter.api.Test
import java.util.Properties

internal class KafkaInntektConsumerTest {

    @Test
    fun `Packet is ignored if not all parameters are present`() {
        val packet = Packet().apply { this.putValue(PacketKeys.INNTEKT, "inntekt") }
        val mock = mockk<PacketStore>()
        runTest(mock, packet) {
            verifyAll { mock wasNot Called }
        }
    }

    @Test
    fun `Packet is handled if all parameters are present`() {
        val packet = Packet("""
            {
                ${PacketKeys.BEHOV_ID}: "behovId",
                ${PacketKeys.SPESIFISERT_INNTEKT}: "spesifisertInntekt",
                ${PacketKeys.PERIODE_RESULTAT}: "periodeResultat",
                ${PacketKeys.SATS_RESULTAT}: "satsResultat",
                ${PacketKeys.MINSTEINNTEKT_RESULTAT}: "minsteinntektResultat"
            }
        """.trimIndent()
        )
        val mock = mockk<PacketStore>().apply {
            every { this@apply.insert(match { it.behovId == "behovId" }) } just Runs
        }
        runTest(mock, packet) {
            verifyAll { mock.insert(match { it.behovId == "behovId" }) }
        }
    }

    private companion object {
        val factory = ConsumerRecordFactory<String, Packet>(
                Topics.DAGPENGER_BEHOV_PACKET_EVENT.name,
                Topics.DAGPENGER_BEHOV_PACKET_EVENT.keySerde.serializer(),
                Topics.DAGPENGER_BEHOV_PACKET_EVENT.valueSerde.serializer()
        )

        val config = Properties().apply {
            this[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
            this[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "dummy:1234"
        }

        fun runTest(store: PacketStore, packet: Packet, testBlock: () -> Unit) {
            InntektPond(store).let {
                TopologyTestDriver(it.buildTopology(), config).use { topologyTestDriver ->
                    topologyTestDriver.pipeInput(factory.create(packet))
                    testBlock()
                }
            }
        }
    }
}