package streams

import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.streams.Topics
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import restapi.streams.InntektPond
import java.util.Properties

internal class KafkaInntektConsumerTest {

    /*
    @Test
    fun `Packet is ignored if not all parameters are present`() {
        runTest(Packet()) {
            verifyAll { }
        }
    }
     */

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

        fun runTest(packet: Packet, testBlock: () -> Unit) {
            InntektPond().let {
                TopologyTestDriver(it.buildTopology(), config).use { topologyTestDriver ->
                    topologyTestDriver.pipeInput(factory.create(packet))
                    testBlock()
                }
            }
        }
    }
}