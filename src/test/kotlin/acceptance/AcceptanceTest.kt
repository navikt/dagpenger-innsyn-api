package acceptance

import no.nav.dagpenger.innsyn.conversion.convertInntektDataIntoUserInformation
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test

@Test
fun convertSuccessfully() {
    convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt)
}