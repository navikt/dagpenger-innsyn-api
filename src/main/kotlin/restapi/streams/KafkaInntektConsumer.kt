package restapi.streams

import no.nav.dagpenger.events.Packet
import no.nav.dagpenger.streams.Pond
import no.nav.dagpenger.streams.streamConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.Predicate
import restapi.APPLICATION_NAME
import restapi.Configuration
import restapi.filteredPackages
import restapi.logger
import java.util.concurrent.TimeUnit

internal class KafkaInntektConsumer(
    private val config: Configuration,
    private val inntektPond: InntektPond
) {

    private val streams: KafkaStreams by lazy {
        KafkaStreams(inntektPond.buildTopology(), this.getConfig()).apply {
            setUncaughtExceptionHandler { _, _ -> System.exit(0) }
        }
    }

    fun start() = streams.start().also { logger.info("Starting up $APPLICATION_NAME kafka consumer") }

    fun stop() = with(streams) {
        close(3, TimeUnit.SECONDS)
        cleanUp()
    }.also {
        logger.info("Shutting down $APPLICATION_NAME kafka consumer")
    }

    private fun getConfig() = streamConfig(
            appId = APPLICATION_NAME,
            bootStapServerUrl = config.kafka.brokers,
            credential = config.kafka.credential()
    )
}

internal class InntektPond : Pond() {
    override val SERVICE_APP_ID: String = APPLICATION_NAME

    override fun filterPredicates(): List<Predicate<String, Packet>> =
            listOf(
                    Predicate { _, packet -> packet.hasField(PacketKeys.BEHOV_ID) },
                    Predicate { _, packet -> !packet.hasProblem() },
                    Predicate { _, packet -> packet.hasField(PacketKeys.INNTEKT) },
                    Predicate { _, packet -> packet.hasField(PacketKeys.MINSTEINNTEKT_RESULTAT) },
                    Predicate { _, packet -> packet.hasField(PacketKeys.MINSTEINNTEKT_INNTEKTSPERIODER) },
                    Predicate { _, packet -> packet.hasField(PacketKeys.PERIODE_RESULTAT) }
            )

    override fun onPacket(packet: Packet) {
        filteredPackages[ packet.getStringValue(PacketKeys.BEHOV_ID) ] = packet
    }
}
