package no.nav.dagpenger.innsyn.conversion

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.events.inntekt.v1.Aktør
import no.nav.dagpenger.events.inntekt.v1.AktørType
import no.nav.dagpenger.events.inntekt.v1.InntektId
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import no.nav.dagpenger.innsyn.expectedEmployerSummaries
import no.nav.dagpenger.innsyn.expectedMonthsIncomeInformation
import no.nav.dagpenger.innsyn.testDataPeriodeResultat
import no.nav.dagpenger.innsyn.testDataSatsResultat
import no.nav.dagpenger.innsyn.testDataSpesifisertInntekt
import no.nav.dagpenger.innsyn.testOrgMapping
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExtractUserInformationTest {

    private val testYearMonths = listOf(YearMonth.of(2001, 1), YearMonth.of(2001, 2), YearMonth.of(2001, 3), YearMonth.of(2001, 4), YearMonth.of(2001, 5),
            YearMonth.of(2001, 9), YearMonth.of(2001, 10), YearMonth.of(2001, 11), YearMonth.of(2001, 12))
    private val testYearMonthsEdge = listOf(YearMonth.of(2000, 10),
            YearMonth.of(2000, 12), YearMonth.of(2001, 1),
            YearMonth.of(2001, 3), YearMonth.of(2001, 4),
            YearMonth.of(2001, 12), YearMonth.of(2002, 1),
            YearMonth.of(1999, 12), YearMonth.of(2000, 1))

    private val empty = SpesifisertInntekt(
            inntektId = InntektId(ULID().nextULID()),
            avvik = emptyList(),
            posteringer = emptyList(),
            ident = Aktør(AktørType.AKTOER_ID, ""),
            manueltRedigert = false,
            timestamp = LocalDateTime.now())

    private val expectedResultTestMonths = listOf(
            EmploymentPeriode(YearMonth.of(2001, 1), YearMonth.of(2001, 5)),
            EmploymentPeriode(YearMonth.of(2001, 9), YearMonth.of(2001, 12)))

    private val expectedResultTestMonthsEdge = listOf(
            EmploymentPeriode(YearMonth.of(1999, 12), YearMonth.of(2000, 1)),
            EmploymentPeriode(YearMonth.of(2000, 10), YearMonth.of(2000, 10)),
            EmploymentPeriode(YearMonth.of(2000, 12), YearMonth.of(2001, 1)),
            EmploymentPeriode(YearMonth.of(2001, 3), YearMonth.of(2001, 4)),
            EmploymentPeriode(YearMonth.of(2001, 12), YearMonth.of(2002, 1)))

    @Test
    fun `Grouping works`() {
        assertEquals(expectedResultTestMonths, groupYearMonthIntoPeriods(testYearMonths))
    }

    @Test
    fun `Grouping works for edge case`() {
        assertEquals(expectedResultTestMonthsEdge, groupYearMonthIntoPeriods(testYearMonthsEdge))
    }

    @Test
    fun `2019-06 and 2018-07 are not successive months`() {
        assertFalse(isSuccessiveMonth(YearMonth.of(2019, 6), YearMonth.of(2018, 7)))
    }

    @Test
    fun `2019-06 and 2019-07 are successive months`() {
        assertTrue(isSuccessiveMonth(YearMonth.of(2019, 6), YearMonth.of(2019, 7)))
    }

    @Test
    fun `Get correct employerSummaries`() {
        assertEquals(expectedEmployerSummaries, getEmployerSummaries(testDataSpesifisertInntekt, emptyMap()))
    }

    @Test
    fun `No månedsinntekter returns empty employerSummaries`() {
        assertEquals(listOf(), getEmployerSummaries(empty, emptyMap()))
    }

    @Test
    fun `Get correct monthsIncomeInformation`() {
        assertEquals(expectedMonthsIncomeInformation, getMonthsIncomeInformation(testDataSpesifisertInntekt, emptyMap()))
    }

    @Test
    fun `No månedsinntekter returns empty monthsIncomeInformation`() {
        assertEquals(listOf(), getMonthsIncomeInformation(empty, emptyMap()))
    }

    @Test
    fun `Mapping with orgID works`() {
        assertEquals(testOrgMapping.values.toList(), convertInntektDataIntoUserInformation(testDataSpesifisertInntekt, testDataPeriodeResultat, testDataSatsResultat, testOrgMapping).employerSummaries.map { it.name })
    }

    @Test
    fun `Get uksats`() {
        assertEquals(3000.0, testDataSatsResultat.ukesats)
    }

    @Test
    fun `Get periodeResultat`() {
        assertEquals(54.0, testDataPeriodeResultat.periodeAntallUker)
    }
}