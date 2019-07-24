package no.nav.dagpenger.innsyn.parsing

import no.nav.dagpenger.innsyn.data.inntekt.Employer
import no.nav.dagpenger.innsyn.data.inntekt.EmployerSummary
import no.nav.dagpenger.innsyn.data.inntekt.EmploymentPeriode
import no.nav.dagpenger.innsyn.data.inntekt.Income
import no.nav.dagpenger.innsyn.data.inntekt.MonthIncomeInformation
import no.nav.dagpenger.innsyn.data.inntekt.ProcessedRequest
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
            totalIncome36 = 155.13,
            totalIncome12 = 80.25,
            employerSummaries = listOf(testDataEmployerSummary),
            monthsIncomeInformation = listOf(testDataMonthIncomeInformation)
    )

    @Test
    fun klaxonParsesIncome() {
        defaultParser
                .parse<Income>(
                        defaultParser
                                .toJsonString(testDataIncome)
                )
    }

    @Test
    fun klaxonParsesEmployer() {
        defaultParser
                .parse<Employer>(
                        defaultParser
                                .toJsonString(testDataEmployer)
                )
    }

    @Test
    fun klaxonParsesMonthIncomeInformation() {
        defaultParser
                .parse<MonthIncomeInformation>(
                        defaultParser
                                .toJsonString(testDataMonthIncomeInformation)
                )
    }

    @Test
    fun klaxonParsesEmployerSummary() {
        defaultParser
                .parse<EmployerSummary>(
                        defaultParser
                                .toJsonString(testDataEmployerSummary)
                )
    }

    @Test
    fun klaxonParsesProcessedRequest() {
        defaultParser
                .parse<ProcessedRequest>(
                        defaultParser
                                .toJsonString(testDataProcessedRequest)
                )
    }
}