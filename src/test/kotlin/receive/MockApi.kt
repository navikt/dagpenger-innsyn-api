package receive

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.innsyn.innsynAPI
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.restapi.streams.InnsynProducer
import no.nav.dagpenger.innsyn.restapi.streams.PacketStore

internal fun MockApi(
        packetStore: PacketStore = mockk(),
        kafkaProducer: InnsynProducer = mockk(),
        jwkProvider: JwkProvider = mockk(),
        healthChecks: List<HealthCheck> = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(packetStore, kafkaProducer, jwkProvider, healthChecks)
    }
}