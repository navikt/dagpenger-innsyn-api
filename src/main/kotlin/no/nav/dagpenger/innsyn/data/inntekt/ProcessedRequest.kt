package no.nav.dagpenger.innsyn.data.inntekt

import java.time.YearMonth

data class ProcessedRequest(
        val personnummer: String,
        val totalIncome: Double,
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
        @no.nav.dagpenger.innsyn.parsing.YearMonth val startDateYearMonth: YearMonth,
        @no.nav.dagpenger.innsyn.parsing.YearMonth val endDateYearMonth: YearMonth
)

data class MonthIncomeInformation(
        @no.nav.dagpenger.innsyn.parsing.YearMonth val month: YearMonth,
        val employers: List<Employer>
)

data class Employer(
        val name: String,
        val orgID: String,
        val incomes: List<Income>
)

data class Income(
        val income: Double,
        val verdikode: String
)
