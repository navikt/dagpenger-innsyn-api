package no.nav.dagpenger.innsyn.processing

import no.nav.dagpenger.innsyn.AUG2018
import no.nav.dagpenger.innsyn.MAR2019
import no.nav.dagpenger.innsyn.SEP2018
import no.nav.dagpenger.innsyn.conversion.objects.Employer
import no.nav.dagpenger.innsyn.conversion.objects.EmployerSummary
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import no.nav.dagpenger.innsyn.conversion.objects.Income
import no.nav.dagpenger.innsyn.conversion.objects.MonthIncomeInformation
import no.nav.dagpenger.innsyn.testDataVirksomhet1
import no.nav.dagpenger.innsyn.testDataVirksomhet2
import no.nav.dagpenger.innsyn.testDataVirksomhet3
import java.time.YearMonth

val expectedResultTestMonths = listOf(
        EmploymentPeriode(YearMonth.of(2001, 1), YearMonth.of(2001, 5)),
        EmploymentPeriode(YearMonth.of(2001, 9), YearMonth.of(2001, 12)))

val expectedResultTestMonthsEdge = listOf(
        EmploymentPeriode(YearMonth.of(1999, 12), YearMonth.of(2000, 1)),
        EmploymentPeriode(YearMonth.of(2000, 10), YearMonth.of(2000, 10)),
        EmploymentPeriode(YearMonth.of(2000, 12), YearMonth.of(2001, 1)),
        EmploymentPeriode(YearMonth.of(2001, 3), YearMonth.of(2001, 4)),
        EmploymentPeriode(YearMonth.of(2001, 12), YearMonth.of(2002, 1)))

val expectedEmployerSummaries = listOf(
        EmployerSummary(name = testDataVirksomhet1.identifikator, orgID = testDataVirksomhet1.identifikator, income = 2983.65, employmentPeriodes = listOf(
                EmploymentPeriode(startDateYearMonth = AUG2018, endDateYearMonth = SEP2018),
                EmploymentPeriode(startDateYearMonth = MAR2019, endDateYearMonth = MAR2019))),
        EmployerSummary(name = testDataVirksomhet2.identifikator, orgID = testDataVirksomhet2.identifikator, income = 68263.91, employmentPeriodes = listOf(
                EmploymentPeriode(startDateYearMonth = AUG2018, endDateYearMonth = SEP2018),
                EmploymentPeriode(startDateYearMonth = MAR2019, endDateYearMonth = MAR2019))),
        EmployerSummary(name = testDataVirksomhet3.identifikator, orgID = testDataVirksomhet3.identifikator, income = 131664.44, employmentPeriodes = listOf(
                EmploymentPeriode(startDateYearMonth = AUG2018, endDateYearMonth = SEP2018),
                EmploymentPeriode(startDateYearMonth = MAR2019, endDateYearMonth = MAR2019))))

val expectedMonthsIncomeInformation = listOf(
        MonthIncomeInformation(month = AUG2018, employers = listOf(
            Employer(name = testDataVirksomhet1.identifikator, orgID = testDataVirksomhet1.identifikator, incomes = listOf(Income(income = 1102.15, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet2.identifikator, orgID = testDataVirksomhet2.identifikator, incomes = listOf(Income(income = 25001.41, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet3.identifikator, orgID = testDataVirksomhet3.identifikator, incomes = listOf(Income(income = 50012.93, beskrivelse = "Fastlønn")))),
                totalIncomeMonth = 76116.49),
        MonthIncomeInformation(month = SEP2018, employers = listOf(
            Employer(name = testDataVirksomhet1.identifikator, orgID = testDataVirksomhet1.identifikator, incomes = listOf(Income(income = 881.51, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet2.identifikator, orgID = testDataVirksomhet2.identifikator, incomes = listOf(Income(income = 11052.5, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet3.identifikator, orgID = testDataVirksomhet3.identifikator, incomes = listOf(Income(income = 33651.11, beskrivelse = "Fastlønn")))),
                totalIncomeMonth = 45585.12),
        MonthIncomeInformation(month = MAR2019, employers = listOf(
            Employer(name = testDataVirksomhet1.identifikator, orgID = testDataVirksomhet1.identifikator, incomes = listOf(Income(income = 999.99, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet2.identifikator, orgID = testDataVirksomhet2.identifikator, incomes = listOf(Income(income = 32210.0, beskrivelse = "Fastlønn"))),
            Employer(name = testDataVirksomhet3.identifikator, orgID = testDataVirksomhet3.identifikator, incomes = listOf(Income(income = 48000.4, beskrivelse = "Fastlønn")))),
                totalIncomeMonth = 81210.39))
