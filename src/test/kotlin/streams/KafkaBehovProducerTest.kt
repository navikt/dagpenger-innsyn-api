package streams

import no.nav.dagpenger.innsyn.restapi.streams.Behov
import no.nav.dagpenger.innsyn.restapi.streams.KafkaInnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.producerConfig
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import java.time.LocalDate
import java.util.concurrent.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private object Kafka {
    val instance by lazy {
        // See https://docs.confluent.io/current/installation/versions-interoperability.html#cp-and-apache-kafka-compatibility
        KafkaContainer("5.0.1").apply { this.start() }
    }
}

internal class KafkaBehovProducerTest {

    @Test
    fun `Produce packet should success`() {
        KafkaInnsynProducer(producerConfig("APP", Kafka.instance.bootstrapServers, null)).apply {
            val metadata = produceEvent(Behov(aktørId = "12345678901", beregningsDato = LocalDate.now())).get(5, TimeUnit.SECONDS)

            assertNotNull(metadata)
            assertTrue(metadata.hasOffset())
            assert(metadata.serializedKeySize() > -1)
            assert(metadata.serializedValueSize() > -1)
        }
    }
}