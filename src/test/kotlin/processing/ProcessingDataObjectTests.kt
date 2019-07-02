package processing

import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals

//@TestInstance
class ProcessingDataObjectTests {

    private val testDataBob = getJSONParsed("Bob")
    private val testDataPeter = getJSONParsed("Peter")

    @Test
    fun InntektForFirstMonthTest() {
        assertEquals(5.83, getInntektForFirstMonth(testDataPeter))
    }

    @Test
    fun AllInntektForOneMonthWithOneInntekt() {
        assertEquals(50.83, getInntektForOneMonth(testDataBob, YearMonth.of(2017, 10)))
    }

    @Test
    fun AllInntektForOneMonthWithTwoInntekts() {
        assertEquals(5600.0, getInntektForOneMonth(testDataBob, YearMonth.of(2017, 9)))
    }

    @Test
    fun AllInntekt36LastMonths() {
        assertEquals(5650.83, getInntektForTheLast36LastMoths(testDataBob))
    }

}