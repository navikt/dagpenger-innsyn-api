package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.events.inntekt.v1.Postering
import no.nav.dagpenger.innsyn.conversion.objects.Employer
import no.nav.dagpenger.innsyn.conversion.objects.EmployerSummary
import no.nav.dagpenger.innsyn.conversion.objects.EmploymentPeriode
import no.nav.dagpenger.innsyn.conversion.objects.Income
import no.nav.dagpenger.innsyn.conversion.objects.MonthIncomeInformation
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
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
