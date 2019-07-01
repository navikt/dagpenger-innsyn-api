package processing

import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import thisisacryforhelp

//@TestInstance
class JSONInputTest {

    val testData = getJSONparsed()

    @Test
    fun GetInntektForFirstMonthTest() {
        assertEquals(calc.getInntektForFirstMonth("99999999999"), 5.83)

    }

    @Test
    fun GetAllInntektForOneMonthWithOneInntekt() {
        val calc = InntektProcessing()
        assertEquals(calc.getInntektForOneMointh(YearMonth.of(2017, 10)), 50.83)
    }

    @Test
    fun GetAllInntektForOneMonthWithTwoInntekts() {
        val calc = InntektProcessing()
        assertEquals(calc.getInntektForOneMointh(YearMonth.of(2017, 9)), 5600.0)
    }

    @Test
    fun GetAllInntekt36LastMonths() {
        val calc = InntektProcessing()
        assertEquals(calc.getIncomForTheLast36LastMoths(), 5650.83)
    }

}