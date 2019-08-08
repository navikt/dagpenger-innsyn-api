package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.events.inntekt.v1.Postering
import no.nav.dagpenger.innsyn.conversion.objects.Employer
import no.nav.dagpenger.innsyn.conversion.objects.EmployerSummary
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import no.nav.dagpenger.innsyn.conversion.objects.Income
import no.nav.dagpenger.innsyn.conversion.objects.MonthIncomeInformation
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.innsyn.conversion.objects.PeriodIncomeInformation
import java.time.YearMonth

data class ArbeidsgiverMaanedInntekt(val arbeidsgiver: String, val maaned: YearMonth, val inntekt: Double)

fun groupYearMonthIntoPeriods(yearMonths: List<YearMonth>): List<EmploymentPeriode> {
    return yearMonths
            .sorted()
            .fold(emptyList(), { list, yearMonth ->
                when {
                    list.isEmpty() -> listOf(EmploymentPeriode(yearMonth, yearMonth))
                    isSuccessiveMonth(list.last().endDateYearMonth, yearMonth) ->
                        list.dropLast(1) + EmploymentPeriode(list.last().startDateYearMonth, yearMonth)
                    else -> list + EmploymentPeriode(yearMonth, yearMonth)
                }
            })
}

fun isSuccessiveMonth(monthOne: YearMonth, monthTwo: YearMonth): Boolean {
    return monthOne.plusMonths(1) == monthTwo
}

fun getEmployerSummaries(spesifisertInntekt: SpesifisertInntekt, orgMapping: Map<String, String>): List<EmployerSummary> {
    return spesifisertInntekt
            .posteringer
            .filter { it.posteringsMåned in get36MonthRange() }
            .map { postering -> ArbeidsgiverMaanedInntekt(
                    inntekt = postering.beløp.toDouble(),
                    maaned = postering.posteringsMåned,
                    arbeidsgiver = postering.virksomhet!!.identifikator
            ) }
            .groupBy { it.arbeidsgiver }
            .map { groupedEmployerInfo -> EmployerSummary(
                    name = orgMapping.getOrElse(groupedEmployerInfo.key) { groupedEmployerInfo.key },
                    orgID = groupedEmployerInfo.key,
                    income = groupedEmployerInfo.value.sumByDouble { it.inntekt },
                    employmentPeriodes = groupYearMonthIntoPeriods(groupedEmployerInfo.value.map { it.maaned })
            ) }
}

fun getMonthsIncomeInformation(spesifisertInntekt: SpesifisertInntekt, orgMapping: Map<String, String>): List<MonthIncomeInformation> {

    return spesifisertInntekt
            .posteringer
            .filter { it.posteringsMåned in get36MonthRange() }
            .groupBy { it.posteringsMåned }
            .map { postering -> MonthIncomeInformation(
                    month = postering.key,
                    totalIncomeMonth = postering.value.sumByDouble { it.beløp.toDouble() }, // TODO: Type safety of BigDecimal vs Double
                    employers = getEmployersForMonth(postering.value, orgMapping)
            ) }
}

fun getEmployersForMonth(posteringer: List<Postering>, orgMapping: Map<String, String>): List<Employer> {
    return posteringer
            .groupBy { it.virksomhet!!.identifikator }
            .mapValues { employerPosteringMap -> employerPosteringMap.value
                    .map { postering -> Income(
                            postering.beløp.toDouble(),
                            postering.posteringsType.beskrivelse
                    ) } }
            .map { employerIncomeMap -> Employer(
                    name = orgMapping.getOrElse(employerIncomeMap.key) { employerIncomeMap.key },
                    orgID = employerIncomeMap.key,
                    incomes = employerIncomeMap.value
            ) }
}

fun getIncomeForPeriods(posteringer: List<Postering>): List<PeriodIncomeInformation> {
    val period1 = PeriodIncomeInformation(
            periodNum = 1,
            startMonth = sisteAvsluttendeKalenderMaaned,
            endMonth = foersteMaaned12,
            totalIncome = posteringer.filter { it.posteringsMåned in get12MonthRange() }
                .sumByDouble { it.beløp.toDouble() }
    )
    val period2 = PeriodIncomeInformation(
            periodNum = 2,
            startMonth = foersteMaaned12.minusMonths(1),
            endMonth = foersteMaaned24,
            totalIncome = posteringer.filter { it.posteringsMåned in get12to24MonthRange() }
                    .sumByDouble { it.beløp.toDouble() }
    )
    val period3 = PeriodIncomeInformation(
            periodNum = 3,
            startMonth = foersteMaaned24.minusMonths(1),
            endMonth = foersteMaaned36,
            totalIncome = posteringer.filter { it.posteringsMåned in get24to36MonthRange() }
                    .sumByDouble { it.beløp.toDouble() }
    )
    return listOf(period1, period2, period3)
}