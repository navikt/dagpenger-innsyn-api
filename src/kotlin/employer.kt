package kotlin

import java.time.LocalDate

data class Employer (
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalIncome: Double,
    val employmentPercentage: Int
)