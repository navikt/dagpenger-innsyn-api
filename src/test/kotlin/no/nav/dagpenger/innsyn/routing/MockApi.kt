package no.nav.dagpenger.innsyn.routing

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.innsyn.innsynAPI
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.BehovProducer
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.monitoring.HealthCheck

internal fun MockApi(
    packetStore: PacketStore = mockk(),
    kafkaProducer: BehovProducer = mockk(),
    jwkProvider: JwkProvider = mockk(),
    healthChecks: List<HealthCheck> = mockk(),
    aktørregisterLookup: AktørregisterLookup = mockk(),
    brønnøysundLookup: BrønnøysundLookup = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(packetStore, kafkaProducer, jwkProvider, healthChecks, aktørregisterLookup, brønnøysundLookup)
    }
}