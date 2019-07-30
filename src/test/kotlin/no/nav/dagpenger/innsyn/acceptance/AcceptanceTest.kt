package no.nav.dagpenger.innsyn.acceptance

import no.nav.dagpenger.innsyn.conversion.convertInntektDataIntoUserInformation
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.expectedEmployerSummaries
import no.nav.dagpenger.innsyn.expectedMonthsIncomeInformation
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import no.nav.dagpenger.innsyn.testDataUserAktoer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AcceptanceTest {

    private val expectedResult = UserInformation(
            personnummer = testDataUserAktoer.identifikator,
            totalIncome36 = 202912.0,
            totalIncome12 = 202912.0,
            employerSummaries = expectedEmployerSummaries,
            monthsIncomeInformation = expectedMonthsIncomeInformation
    )

    @Test
    fun `Successfully convert SpesifisertInntekt to UserInformation`() {
        assertEquals(expectedResult, convertInntektDataIntoUserInformation(spesifisertInntekt = testDataSpesifisertInntekt))
    }
}
