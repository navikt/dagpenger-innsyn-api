package no.nav.dagpenger.innsyn.conversion.objects

import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.innsyn.YearMonthParsing
import java.time.YearMonth

data class UserInformation(
    val personnummer: String,
    val totalIncome36: Double,
    val totalIncome12: Double,
    val employerSummaries: List<EmployerSummary>,
    val monthsIncomeInformation: List<MonthIncomeInformation>
)

data class EmployerSummary(
    val name: String,
    val orgID: String,
    val income: Double,
    val employmentPeriodes: List<EmploymentPeriode>
)

data class EmploymentPeriode(
        @YearMonthParsing val startDateYearMonth: YearMonth,
        @YearMonthParsing val endDateYearMonth: YearMonth
)

data class MonthIncomeInformation(
        @YearMonthParsing val month: YearMonth,
        val employers: List<Employer>,
        val totalIncomeMonth: Double
)

data class Employer(
    val name: String,
    val orgID: String,
    val incomes: List<Income>
)

data class Income(
    val income: Double,
    val beskrivelse: String
)
