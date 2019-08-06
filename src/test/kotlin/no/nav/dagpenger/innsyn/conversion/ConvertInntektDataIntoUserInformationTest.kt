package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.expectedFinalResult
import no.nav.dagpenger.innsyn.testDataMinsteinntektResultat
import no.nav.dagpenger.innsyn.testDataPeriodeResultat
import no.nav.dagpenger.innsyn.testDataSatsResultat
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConvertInntektDataIntoUserInformationTest {

    @Test
    fun `Successfully convert SpesifisertInntekt to UserInformation`() {
        assertEquals(expectedFinalResult, convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt, periodeResultat = testDataPeriodeResultat, satsResultat = testDataSatsResultat, kvalifisertResultat = testDataMinsteinntektResultat, orgMapping = emptyMap()))
    }
}
