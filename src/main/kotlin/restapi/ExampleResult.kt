package restapi

import data.inntekt.Employer
import data.inntekt.EmployerSummary
import data.inntekt.EmploymentPeriode
import data.inntekt.Income
import data.inntekt.MonthIncomeInformation
import data.inntekt.ProcessedRequest
import java.time.YearMonth

//TODO: Move to test when we have MVP
fun getExample(): ProcessedRequest {

    val fastloennConst = "Fastlønn"

    return ProcessedRequest(
            personnummer = "18069637988",
            totalIncome = 25542.12,
            totalIncome12 = 122234.3,
            employerSummaries = listOf(
                    EmployerSummary(
                            name = "NAV",
                            orgID = "451123",
                            income = 7300.0,
                            employmentPeriodes = listOf(
                                    EmploymentPeriode(
                                            startDateYearMonth = YearMonth.of(2019, 4),
                                            endDateYearMonth = YearMonth.now()
                                    )

                            )
                    ),
                    EmployerSummary(
                            name = "BEKK",
                            orgID = "112300",
                            income = 11231.120,
                            employmentPeriodes = listOf(
                                    EmploymentPeriode(
                                            startDateYearMonth = YearMonth.of(2019, 4),
                                            endDateYearMonth = YearMonth.now()
                                    )

                            )
                    ),
                    EmployerSummary(
                            name = "Visma Consulting",
                            orgID = "661298",
                            income = 7011.00,
                            employmentPeriodes = listOf(
                                    EmploymentPeriode(
                                            startDateYearMonth = YearMonth.of(2019, 4),
                                            endDateYearMonth = YearMonth.now()
                                    ),
                                    EmploymentPeriode(
                                            startDateYearMonth = YearMonth.of(2019, 4),
                                            endDateYearMonth = YearMonth.now()
                                    )

                            )
                    )
            ),
            monthsIncomeInformation = listOf(
                    MonthIncomeInformation(
                            month = YearMonth.of(2019, 1),
                            employers = listOf(
                                    Employer(
                                            name = "NAV",
                                            orgID = "451123",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            verdikode = fastloennConst
                                                    ),
                                                    Income(
                                                            income = 7300.0,
                                                            verdikode = "Feriepenger"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "BEKK",
                                            orgID = "112300",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            verdikode = "Bil"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "Visma Consulting",
                                            orgID = "661298",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            verdikode = fastloennConst
                                                    )
                                            )
                                    )
                            )
                    ),
                    MonthIncomeInformation(
                            month = YearMonth.of(2018, 1),
                            employers = listOf(
                                    Employer(
                                            name = "NAV",
                                            orgID = "451123",
                                            incomes = listOf(
                                                    Income(
                                                            income = 8300.0,
                                                            verdikode = fastloennConst
                                                    ),
                                                    Income(
                                                            income = 3300.0,
                                                            verdikode = "Feriepenger"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "BEKK",
                                            orgID = "112300",
                                            incomes = listOf(
                                                    Income(
                                                            income = 5552.0,
                                                            verdikode = "Bil"
                                                    )
                                            )
                                    )
                            )
                    )
            )
    )
}
