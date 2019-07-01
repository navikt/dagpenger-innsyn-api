package processing

import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals

//@TestInstance
class ProcessingDataObjectTests {

    val testDataBob = getJSONParsed("Bob")
    val testDataPeter = getJSONParsed("Peter")

    @Test
    fun InntektForFirstMonthTest() {
        assertEquals(getInntektForFirstMonth(testDataPeter), 5.83)
    }

    @Test
    fun AllInntektForOneMonthWithOneInntekt() {
        assertEquals(getInntektForOneMonth(testDataBob, YearMonth.of(2017, 10)), 50.83)
    }

    @Test
    fun AllInntektForOneMonthWithTwoInntekts() {
        assertEquals(getInntektForOneMonth(testDataBob, YearMonth.of(2017, 9)), 5600.0)
    }

    @Test
    fun AllInntekt36LastMonths() {
        assertEquals(getIncomForTheLast36LastMoths(testDataBob), 5650.83)
    }
    @Test
    fun GetListOfArbeidsgiverTest() {

        assertEquals(getInntektPerArbeidsgiverList(testDataBob)[0].arbeidsgiver,"222222" )

    }
    @Test
    fun GetListOfInntektForEachArbeidsgiverTest() {

        assertEquals(getInntektPerArbeidsgiverList(testDataBob).get(0).inntekt, 5099.00)
        assertEquals(getInntektPerArbeidsgiverList(testDataBob).get(1).inntekt,501.00 )
    }
    @Test
    fun GetTotalListOfInntektForEachArbeidsgiverTest() {

        assertEquals(getTotalInntektPerArbeidsgiver(testDataBob).get(0).inntekt, 5149.83)
        assertEquals(getTotalInntektPerArbeidsgiver(testDataBob).get(1).inntekt, 501.0)
    }

}