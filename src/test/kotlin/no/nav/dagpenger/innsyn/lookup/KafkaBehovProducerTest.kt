package no.nav.dagpenger.innsyn.lookup

import org.testcontainers.containers.KafkaContainer

private object Kafka {
    val instance by lazy {
        // See https://docs.confluent.io/current/installation/versions-interoperability.html#cp-and-apache-kafka-compatibility
        KafkaContainer("5.0.1").apply { this.start() }
    }
}

internal class KafkaBehovProducerTest {
    /*
    @Test
    fun `Produce packet should success`() {
        KafkaBehovProducer(producerConfig("APP", Kafka.instance.bootstrapServers, null)).apply {
            val metadata = produceEvent(Behov(aktørId = "12345678901", beregningsDato = LocalDate.now())).get(5, TimeUnit.SECONDS)

            assertNotNull(metadata)
            assertTrue(metadata.hasOffset())
            assert(metadata.serializedKeySize() > -1)
            assert(metadata.serializedValueSize() > -1)
        }
    }
     */
}