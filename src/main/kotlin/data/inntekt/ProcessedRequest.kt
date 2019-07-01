package data.inntekt

import java.time.YearMonth

data class ProcessedRequest(
        val totalIncome: Double,
        val employerSummaries: List<EmployerSummary>,
        val monthsIncomeInformation: List<MonthIncomeInformation>
)

data class EmployerSummary(
        val name: String,
        val orgID: String,
        val income: Double
)

data class MonthIncomeInformation(
        val month: YearMonth,
        val employers: List<Employer>
)

data class Employer(
        val name: String,
        val orgID: String,
        val income: Double
)