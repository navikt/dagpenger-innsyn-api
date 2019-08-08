package no.nav.dagpenger.innsyn.conversion.objects

import java.time.YearMonth

data class UserInformation(
    val totalIncome36: Double,
    val totalIncome12: Double,
    val employerSummaries: List<EmployerSummary>,
    val monthsIncomeInformation: List<MonthIncomeInformation>,
    val periodIncome: List<PeriodIncomeInformation>,
    val oppfyllerMinstekrav: Boolean,
    val periodeAntalluker: Double,
    val ukeSats: Double
)

data class EmployerSummary(
    val name: String,
    val orgID: String,
    val income: Double,
    val employmentPeriodes: List<EmploymentPeriode>
)

data class EmploymentPeriode(
    val startDateYearMonth: YearMonth,
    val endDateYearMonth: YearMonth
)

data class PeriodIncomeInformation(
    val periodNum: Int,
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val totalIncome: Double
)

data class MonthIncomeInformation(
    val month: YearMonth,
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
