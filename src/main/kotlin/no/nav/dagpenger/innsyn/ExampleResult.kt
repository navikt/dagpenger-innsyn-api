package no.nav.dagpenger.innsyn

import no.nav.dagpenger.innsyn.conversion.objects.Employer
import no.nav.dagpenger.innsyn.conversion.objects.EmployerSummary
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import no.nav.dagpenger.innsyn.conversion.objects.Income
import no.nav.dagpenger.innsyn.conversion.objects.MonthIncomeInformation
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import java.time.YearMonth

// TODO: Move to test when we have MVP
fun getExample(): UserInformation {

    val fastloennConst = "Fastlønn"

    return UserInformation(
            personnummer = "18069637988",
            totalIncome36 = 25542.12,
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
                            totalIncomeMonth = (7300.0 + 7300.0 + 7300.0),
                            employers = listOf(
                                    Employer(
                                            name = "NAV",
                                            orgID = "451123",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            beskrivelse = fastloennConst
                                                    ),
                                                    Income(
                                                            income = 7300.0,
                                                            beskrivelse = "Feriepenger"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "BEKK",
                                            orgID = "112300",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            beskrivelse = "Bil"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "Visma Consulting",
                                            orgID = "661298",
                                            incomes = listOf(
                                                    Income(
                                                            income = 7300.0,
                                                            beskrivelse = fastloennConst
                                                    )
                                            )
                                    )
                            )
                    ),
                    MonthIncomeInformation(
                            month = YearMonth.of(2018, 1),
                            totalIncomeMonth = (8300.0 + 3300 + 5552),
                            employers = listOf(
                                    Employer(
                                            name = "NAV",
                                            orgID = "451123",
                                            incomes = listOf(
                                                    Income(
                                                            income = 8300.0,
                                                            beskrivelse = fastloennConst
                                                    ),
                                                    Income(
                                                            income = 3300.0,
                                                            beskrivelse = "Feriepenger"
                                                    )
                                            )
                                    ),
                                    Employer(
                                            name = "BEKK",
                                            orgID = "112300",
                                            incomes = listOf(
                                                    Income(
                                                            income = 5552.0,
                                                            beskrivelse = "Bil"
                                                    )
                                            )
                                    )
                            )
                    )
            )
    )
}
