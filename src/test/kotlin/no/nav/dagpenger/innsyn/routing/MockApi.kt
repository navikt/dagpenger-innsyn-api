package no.nav.dagpenger.innsyn.routing

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.innsyn.innsynAPI
import no.nav.dagpenger.innsyn.lookup.AktørregisterLookup
import no.nav.dagpenger.innsyn.lookup.InntektLookup
import no.nav.dagpenger.innsyn.monitoring.HealthCheck

internal fun MockApi(
    jwkProvider: JwkProvider = mockk(),
    healthChecks: List<HealthCheck> = mockk(),
    aktørregisterLookup: AktørregisterLookup = mockk(),
    inntektLookup: InntektLookup = mockk()
): Application.() -> Unit {
    return fun Application.() {
        innsynAPI(jwkProvider, healthChecks, aktørregisterLookup, inntektLookup)
    }
}