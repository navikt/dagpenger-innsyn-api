package streams

import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import restapi.streams.Behov
import restapi.streams.KafkaInnsynProducer
import restapi.streams.producerConfig
import java.time.LocalDate
import java.util.concurrent.TimeUnit
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
            val metadata = produceEvent(Behov("12345678901", LocalDate.now(), "token")).get(5, TimeUnit.SECONDS)

            assertNotNull(metadata)
            assertTrue(metadata.hasOffset())
            assert(metadata.serializedKeySize() > -1)
            assert(metadata.serializedValueSize() > -1)
        }
    }
}