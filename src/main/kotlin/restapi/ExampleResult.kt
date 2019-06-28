package restapi

import data.json.Employer
import data.json.EmployerSummary
import data.json.MonthIncomeInformation
import data.json.ProcessedRequest
import java.time.YearMonth

fun getExample() : ProcessedRequest {

    return ProcessedRequest(
            totalIncome=25542.12,
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
