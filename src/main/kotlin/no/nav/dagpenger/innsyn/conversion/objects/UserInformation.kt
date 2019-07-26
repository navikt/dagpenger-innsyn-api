package no.nav.dagpenger.innsyn.conversion.objects

<<<<<<< HEAD
=======
import no.nav.dagpenger.innsyn.YearMonthParsing
>>>>>>> e7ce4995e64c7e18ff12395f4033b9dba7cb2065
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
<<<<<<< HEAD
        val startDateYearMonth: YearMonth,
        val endDateYearMonth: YearMonth
)

data class MonthIncomeInformation(
        val month: YearMonth,
        val employers: List<Employer>,
        val totalIncomeMonth: Double
=======
    @YearMonthParsing val startDateYearMonth: YearMonth,
    @YearMonthParsing val endDateYearMonth: YearMonth
)

data class MonthIncomeInformation(
    @YearMonthParsing val month: YearMonth,
    val employers: List<Employer>,
    val totalIncomeMonth: Double
>>>>>>> e7ce4995e64c7e18ff12395f4033b9dba7cb2065
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
