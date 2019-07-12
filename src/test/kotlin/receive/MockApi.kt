package receive

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import restapi.innsynAPI
import restapi.streams.InnsynProducer
import restapi.streams.PacketStore

internal fun MockApi(
        kafkaProducer: InnsynProducer = mockk(),
        jwkProvider: JwkProvider = mockk(),
        packetStore: PacketStore = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(kafkaProducer, jwkProvider, packetStore)
    }
}