package restapi.streams

import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.streams.PacketDeserializer
import no.nav.dagpenger.streams.Topics
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.RetriableException
import org.apache.kafka.common.serialization.StringDeserializer
import restapi.APPLICATION_NAME
import restapi.logger
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

// TODO: Add no.nav.dagpenger.plain to dependencies
val defaultConsumerConfig = Properties().apply {
    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PacketDeserializer::class.java.name)
}

fun commonConfig(bootstrapServers: String, credential: no.nav.dagpenger.streams.KafkaCredential? = null): Properties {
    return Properties().apply {
        put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    }
}

internal fun consumerConfig(
        groupId: String,
        bootstrapServerUrl: String,
        credential: no.nav.dagpenger.streams.KafkaCredential? = null,
        properties: Properties = defaultConsumerConfig
): Properties {
    return Properties().apply {
        putAll(properties)
        putAll(commonConfig(bootstrapServerUrl, credential))
        put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    }
}

internal class KafkaInntektConsumer(kafkaProps: Properties) {

    private val kafkaConsumer = KafkaConsumer<String, Packet>(kafkaProps, Topics.DAGPENGER_BEHOV_PACKET_EVENT.keySerde.deserializer(), Topics.DAGPENGER_BEHOV_PACKET_EVENT.valueSerde.deserializer())

    fun start() = kafkaConsumer
            .subscribe(listOf(Topics.DAGPENGER_BEHOV_PACKET_EVENT.name))
            .also { logger.info ("Starting up $APPLICATION_NAME kafka consumer") }

    fun stop() = with(kafkaConsumer) {
        close(Duration.ofSeconds(3))
    }.also {
        logger.info ("Shutting down $APPLICATION_NAME kafka consumer")
    }

    fun consume(behovId: String): Packet{
        while (true) {
            try {
                val records = kafkaConsumer.poll(Duration.of(100, ChronoUnit.MILLIS))
                return records.asSequence()
                        .onEach { r -> logger.info("Received packet with key ${r.key()} and will test it against filters.") }
                        .filter { r -> r.value().getStringValue(PacketKeys.BEHOV_ID) == behovId}
                        .filterNot { r -> r.value().hasProblem() }
                        .filter { r -> r.value().hasField(PacketKeys.INNTEKT)}
                        .filter { r -> r.value().hasField(PacketKeys.MINSTEINNTEKT_RESULTAT)}
                        .filter { r -> r.value().hasField(PacketKeys.MINSTEINNTEKT_INNTEKTSPERIODER)}
                        .filter { r -> r.value().hasField(PacketKeys.PERIODE_RESULTAT)}
                        .first()
                        .value()
            } catch (e: RetriableException) {
                logger.warn("Kafka threw a retriable exception, will retry", e)
            }
        }
    }

}