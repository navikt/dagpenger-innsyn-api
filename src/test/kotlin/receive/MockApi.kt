package receive

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import restapi.innsynAPI
import restapi.streams.KafkaInnsynProducer

internal fun MockApi(
        kafkaInnsynProducer: KafkaInnsynProducer = mockk(),
        jwkProvider: JwkProvider = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(kafkaInnsynProducer, jwkProvider)
    }
}