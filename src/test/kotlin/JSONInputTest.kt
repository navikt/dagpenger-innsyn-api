import objects.IncomeCalculator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.YearMonth
import kotlin.test.assertEquals

//@TestInstance
class JSONInputTest {
    @Test
    fun GetIncomeForFirstMonthTest() {
        val calc = IncomeCalculator()
        assertEquals(calc.getIncomeForFirstMonth("99999999999"), 5.83)

    }

    @Test
    fun GetAllIncomeForOneMonthWithOneIncome(){
        val calc = IncomeCalculator()
        assertEquals(calc.getIncomeForOneMointh(YearMonth.of(2017,10)), 50.83)
    }
    @Test
    fun GetAllIncomeForOneMonthWithTwoIncomes(){
        val calc = IncomeCalculator()
        assertEquals(calc.getIncomeForOneMointh(YearMonth.of(2017,9)), 5600.0)
    }
    @Test
    fun GetAllIncome36LastMonths(){
        val calc = IncomeCalculator()
        assertEquals(calc.getIncomForTheLast36LastMoths(), 5650.83)
    }

}