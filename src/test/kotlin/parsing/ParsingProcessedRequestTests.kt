package parsing

import com.beust.klaxon.Klaxon
import data.inntekt.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.YearMonth

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParsingProcessedRequestTests {

    private val testDataIncome = Income(
            income = 155.13,
            verdikode = "Total Lønnsinntekt"
    )

    private val testDataEmployer = Employer(
            name = "NAV",
            orgID = "114235",
            incomes = listOf(testDataIncome)
    )

    private val testDataMonthIncomeInformation = MonthIncomeInformation(
            month = YearMonth.of(2019, 1),
            employers = listOf(testDataEmployer)
    )

    private val testDataEmployerSummary = EmployerSummary(
            name = "NAV",
            orgID = "114235",
            income = 155.13,
            employmentPeriodes = listOf(EmploymentPeriode(
                    startDateYearMonth = YearMonth.of(2019, 1),
                    endDateYearMonth = YearMonth.of(2019, 3)
            )
            )
    )

    private val testDataProcessedRequest = ProcessedRequest(
            personnummer = "131165542135",
            totalIncome = 155.13,
            employerSummaries = listOf(testDataEmployerSummary),
            monthsIncomeInformation = listOf(testDataMonthIncomeInformation)
    )

    @Test
    fun KlaxonParsesIncome() {
        println(testDataIncome)
        Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .parse<Income>(
                        Klaxon()
                                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                                .fieldConverter(parsing.Double::class, doubleParser)
                                .toJsonString(testDataIncome)
                )
    }

    @Test
    fun KlaxonParsesEmployer() {
        println(testDataEmployer)
        Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .parse<Employer>(
                        Klaxon()
                                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                                .fieldConverter(parsing.Double::class, doubleParser)
                                .toJsonString(testDataEmployer)
                )
    }

    @Test
    fun KlaxonParsesMonthIncomeInformation() {
        println(testDataMonthIncomeInformation)
        println(Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .toJsonString(testDataMonthIncomeInformation)
        )
        Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .parse<MonthIncomeInformation>(
                        Klaxon()
                                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                                .fieldConverter(parsing.Double::class, doubleParser)
                                .toJsonString(testDataMonthIncomeInformation)
                )
    }

    @Test
    fun KlaxonParsesEmployerSummary() {
        println(testDataEmployerSummary)
        println(Klaxon().fieldConverter(parsing.Double::class, doubleParser).toJsonString(testDataEmployerSummary))
        println("still here?")
        Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .parse<EmployerSummary>(
                        Klaxon()
                                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                                .fieldConverter(parsing.Double::class, doubleParser)
                                .toJsonString(testDataEmployerSummary)
                )
    }

    @Test
    fun KlaxonParsesProcessedRequest() {
        println("Test")
        println(testDataProcessedRequest.toString())
        println("MoreTest")
        println(Klaxon()
                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .toJsonString(testDataProcessedRequest))
        Klaxon().fieldConverter(parsing.YearMonth::class, yearMonthParser)
                .fieldConverter(parsing.Double::class, doubleParser)
                .parse<ProcessedRequest>(
                        Klaxon()
                                .fieldConverter(parsing.YearMonth::class, yearMonthParser)
                                .fieldConverter(parsing.Double::class, doubleParser)
                                .toJsonString(testDataProcessedRequest)
                )
    }
}