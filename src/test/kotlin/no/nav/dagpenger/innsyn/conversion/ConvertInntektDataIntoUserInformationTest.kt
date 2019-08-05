package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.expectedFinalResult
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConvertInntektDataIntoUserInformationTest {

    @Test
    fun `Successfully convert SpesifisertInntekt to UserInformation`() {
        println(moshiInstance.adapter(UserInformation::class.java).toJson(expectedFinalResult))
        println(moshiInstance.adapter(UserInformation::class.java).toJson(convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt, orgMapping = emptyMap())))
        assertEquals(expectedFinalResult, convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt, orgMapping = emptyMap()))
    }
}
