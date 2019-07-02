package restapi

import data.inntekt.Employer
import data.inntekt.EmployerSummary
import data.inntekt.MonthIncomeInformation
import data.inntekt.ProcessedRequest
import java.time.YearMonth

fun getExample(): ProcessedRequest {

    return ProcessedRequest(
            personnummer = "1432523",
            totalIncome = 25542.12,
            employerSummaries = listOf(
                    EmployerSummary(
                            name = "NAV",
                            orgID = "451123",
                            income = 7300.0
                    ),
                    EmployerSummary(
                            name = "BEKK",
                            orgID = "112300",
                            income = 11231.12
                    ),
                    EmployerSummary(
                            name = "Visma Consulting",
                            orgID = "661298",
                            income = 7011.0
                    )
            ),
            monthsIncomeInformation = listOf(
                    MonthIncomeInformation(
                            month = YearMonth.now(),
                            employers = listOf(
                                    Employer(
                                            name = "NAV",
                                            orgID = "451123",
                                            income = 7300.0
                                    ),
                                    Employer(
                                            name = "BEKK",
                                            orgID = "112300",
                                            income = 11231.12
                                    ),
                                    Employer(
                                            name = "Visma Consulting",
                                            orgID = "661298",
                                            income = 7011.0
                                    )
                            )
                    )
            )
    )
}
