package receive

import io.ktor.application.Application
import io.mockk.mockk
import restapi.innsynAPI
import restapi.streams.KafkaInnsynProducer

internal fun MockApi(
    kafkaInnsynProducer: KafkaInnsynProducer = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(kafkaInnsynProducer)
    }
}