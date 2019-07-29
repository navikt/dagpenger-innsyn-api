package no.nav.dagpenger.innsyn.processing

import no.nav.dagpenger.innsyn.conversion.groupYearMonthIntoPeriods
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.YearMonth
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProcessingDataObjectTests {

    // private val testDataBob = getJSONParsed("Bob")

    private val testYearMonths = listOf(YearMonth.of(2001, 1), YearMonth.of(2001, 2), YearMonth.of(2001, 3), YearMonth.of(2001, 4), YearMonth.of(2001, 5),
            YearMonth.of(2001, 9), YearMonth.of(2001, 10), YearMonth.of(2001, 11), YearMonth.of(2001, 12))
    private val testYearMonthsEdge = listOf(YearMonth.of(2000, 10),
            YearMonth.of(2000, 12), YearMonth.of(2001, 1),
            YearMonth.of(2001, 3), YearMonth.of(2001, 4),
            YearMonth.of(2001, 12), YearMonth.of(2002, 1),
            YearMonth.of(1999, 12), YearMonth.of(2000, 1))

    private val expectedResultTestMonths = listOf(
            EmploymentPeriode(YearMonth.of(2001, 1), YearMonth.of(2001, 5)),
            EmploymentPeriode(YearMonth.of(2001, 9), YearMonth.of(2001, 12)))
    private val expectedResultTestMonthsEdge = listOf(
            EmploymentPeriode(YearMonth.of(1999, 12), YearMonth.of(2000, 1)),
            EmploymentPeriode(YearMonth.of(2000, 10), YearMonth.of(2000, 10)),
            EmploymentPeriode(YearMonth.of(2000, 12), YearMonth.of(2001, 1)),
            EmploymentPeriode(YearMonth.of(2001, 3), YearMonth.of(2001, 4)),
            EmploymentPeriode(YearMonth.of(2001, 12), YearMonth.of(2002, 1)))

    private val expectedPeriodsGabriel = "[ArbeidsgiverOgPeriode(arbeidsgiver=222222, perioder=[EmploymentPeriode(startDateYearMonth=2017-09, endDateYearMonth=2017-09), EmploymentPeriode(startDateYearMonth=2017-12, endDateYearMonth=2017-12)]), ArbeidsgiverOgPeriode(arbeidsgiver=2222221, perioder=[EmploymentPeriode(startDateYearMonth=2017-09, endDateYearMonth=2017-09)]), ArbeidsgiverOgPeriode(arbeidsgiver=55555, perioder=[EmploymentPeriode(startDateYearMonth=2017-10, endDateYearMonth=2017-10)]), ArbeidsgiverOgPeriode(arbeidsgiver=666666, perioder=[EmploymentPeriode(startDateYearMonth=2017-10, endDateYearMonth=2017-10)]), ArbeidsgiverOgPeriode(arbeidsgiver=11111, perioder=[EmploymentPeriode(startDateYearMonth=2017-11, endDateYearMonth=2017-12)])]" // TODO: Fix this nonsense

    @Test
    fun allInntekt36LastMonths() {
    }

    @Test
    fun getListOfArbeidsgiverTest() {
    }

    @Test
    fun getListOfInntektForEachArbeidsgiverTest() {
    }

    @Test
    fun getTotalListOfInntektForEachArbeidsgiverTest() {
    }

    @Test
    fun checkGroupingWorks() {
        assertEquals(expectedResultTestMonths, groupYearMonthIntoPeriods(testYearMonths))
    }

    @Test
    fun checkGroupingWorksEdgeCase() {
        assertEquals(expectedResultTestMonthsEdge, groupYearMonthIntoPeriods(testYearMonthsEdge))
    }
}