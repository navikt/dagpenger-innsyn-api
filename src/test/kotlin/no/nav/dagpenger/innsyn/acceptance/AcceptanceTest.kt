package no.nav.dagpenger.innsyn.acceptance

import no.nav.dagpenger.innsyn.conversion.convertInntektDataIntoUserInformation
import no.nav.dagpenger.innsyn.expectedFinalResult
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AcceptanceTest {

    @Test
    fun `Successfully convert SpesifisertInntekt to UserInformation`() {
        assertEquals(expectedFinalResult, convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt))
    }
}
