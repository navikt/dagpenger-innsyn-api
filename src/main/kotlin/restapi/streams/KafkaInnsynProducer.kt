package restapi.streams

import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.streams.Topics
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import restapi.APPLICATION_NAME
import restapi.logger
import java.util.concurrent.Future

internal fun producerConfig(
        appId: String,
        bootStapServerUrl: String,
        credential: KafkaCredential? = null
): Properties {
    return Properties().apply {
        putAll(
                listOf(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootStapServerUrl,
                        ProducerConfig.CLIENT_ID_CONFIG to appId,
                        ProducerConfig.ACKS_CONFIG to "all",
                        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                        ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE.toString(),
                        ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to "5", // kafka 2.0 >= 1.1 so we can keep this as 5 instead of 1
                        ProducerConfig.COMPRESSION_TYPE_CONFIG to "snappy",
                        ProducerConfig.LINGER_MS_CONFIG to "20",
                        ProducerConfig.BATCH_SIZE_CONFIG to 32.times(1024).toString() // 32Kb (default is 16 Kb)
                )
        )

    }
}

internal interface InnsynProducer {
    fun produceEvent(behov: Behov): Future<RecordMetadata>
}

internal class KafkaInnsynProducer(kafkaProps: Properties) : InnsynProducer {

    private val kafkaProducer = KafkaProducer<String, Packet>(kafkaProps, Topics.DAGPENGER_BEHOV_PACKET_EVENT.keySerde.serializer(), Topics.DAGPENGER_BEHOV_PACKET_EVENT.valueSerde.serializer())

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Closing $APPLICATION_NAME Kafka producer")
            kafkaProducer.flush()
            kafkaProducer.close()
            logger.info("done! ")
        })
    }


    override fun produceEvent(behov: Behov): Future<RecordMetadata> {
        return kafkaProducer.send(
                ProducerRecord(Topics.DAGPENGER_BEHOV_PACKET_EVENT.name, behov.behovId, Behov.toPacket(behov))
        ) { metadata, exception ->
            exception?.let {logger.error("Failed to produce dagpenger behov")}
            metadata?.let {logger.info ("Produced dagpenger behov on topic ${metadata.topic()} to offset ${metadata.offset()} with the key key") }
        }
    }
}