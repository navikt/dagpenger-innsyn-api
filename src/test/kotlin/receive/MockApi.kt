package receive

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import restapi.innsynAPI
import restapi.streams.InnsynProducer

internal fun MockApi(
        kafkaProducer: InnsynProducer = mockk(),
        jwkProvider: JwkProvider = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(kafkaProducer, jwkProvider)
    }
}