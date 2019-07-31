package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConvertInntektDataIntoUserInformationTest {

    @Test
    fun `Successfully convert SpesifisertInntekt to UserInformation`() {
        assertEquals(expectedFinalResult, getUserInformation(spesifisertInntekt = testDataSpesifisertInntekt, brønnøysundLookup = BrønnøysundLookup()))
    }
}
