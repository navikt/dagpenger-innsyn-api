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

}