package processing

import data.inntekt.EmploymentPeriode
import org.junit.jupiter.api.Test
import parsing.getJSONParsed
import java.time.YearMonth
import kotlin.test.assertEquals

//@TestInstance
class ProcessingDataObjectTests {

    private val testDataBob = getJSONParsed("Bob")
    private val testDataPeter = getJSONParsed("Peter")
    private val testDataGabriel = getJSONParsed("Gabriel")

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

    private val expectedPeriodsGabriel = "[ArbeidsgiverOgPeriode(arbeidsgiver=222222, perioder=[EmploymentPeriode(startDateYearMonth=2017-09, endDateYearMonth=2017-09), EmploymentPeriode(startDateYearMonth=2017-12, endDateYearMonth=2017-12)]), ArbeidsgiverOgPeriode(arbeidsgiver=2222221, perioder=[EmploymentPeriode(startDateYearMonth=2017-09, endDateYearMonth=2017-09)]), ArbeidsgiverOgPeriode(arbeidsgiver=55555, perioder=[EmploymentPeriode(startDateYearMonth=2017-10, endDateYearMonth=2017-10)]), ArbeidsgiverOgPeriode(arbeidsgiver=666666, perioder=[EmploymentPeriode(startDateYearMonth=2017-10, endDateYearMonth=2017-10)]), ArbeidsgiverOgPeriode(arbeidsgiver=11111, perioder=[EmploymentPeriode(startDateYearMonth=2017-11, endDateYearMonth=2017-12)])]"//TODO: Fix this nonsense

    @Test
    fun inntektForFirstMonthTest() {
        assertEquals(getInntektForFirstMonth(testDataPeter), 5.83)
    }

    @Test
    fun allInntektForOneMonthWithOneInntekt() {
        assertEquals(50.83, getInntektForOneMonth(testDataBob, YearMonth.of(2017, 10)))
    }

    @Test
    fun allInntektForOneMonthWithTwoInntekts() {
        assertEquals(5600.0, getInntektForOneMonth(testDataBob, YearMonth.of(2017, 9)))
    }

    @Test
    fun allInntekt36LastMonths() {
        assertEquals(5650.83, getInntektForTheLast36LastMoths(testDataBob))
    }

    @Test
    fun getListOfArbeidsgiverTest() {
        assertEquals("222222", getInntektPerArbeidsgiverList(testDataBob)[0].arbeidsgiver)
    }

    @Test
    fun getListOfInntektForEachArbeidsgiverTest() {

        assertEquals(5099.00, getInntektPerArbeidsgiverList(testDataBob).get(0).inntekt)
        assertEquals(501.00, getInntektPerArbeidsgiverList(testDataBob).get(1).inntekt)
    }

    @Test
    fun getTotalListOfInntektForEachArbeidsgiverTest() {

        assertEquals(5149.83, getTotalInntektPerArbeidsgiver(testDataBob).get(0).inntekt)
        assertEquals(501.0, getTotalInntektPerArbeidsgiver(testDataBob).get(1).inntekt)
    }

    @Test
    fun checkGroupingWorks() {
        assertEquals(expectedResultTestMonths, groupYearMonthIntoPeriods(testYearMonths))
    }

    @Test
    fun checkGroupingWorksEdgeCase() {
        assertEquals(expectedResultTestMonthsEdge, groupYearMonthIntoPeriods(testYearMonthsEdge))
    }

    @Test
    fun checkPeriodSortingWorks() {
        assertEquals(expectedPeriodsGabriel, getPeriodForEachEmployer(testDataGabriel).toString())
    }


}