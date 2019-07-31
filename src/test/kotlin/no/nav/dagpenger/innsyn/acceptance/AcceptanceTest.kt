package no.nav.dagpenger.innsyn.acceptance

import no.nav.dagpenger.innsyn.conversion.getUserInformation
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test

@Test
fun convertSuccessfully() {
    getUserInformation(spesifisertInntekt = testDataSpesifisertInntekt, brønnøysundLookup = BrønnøysundLookup())
}