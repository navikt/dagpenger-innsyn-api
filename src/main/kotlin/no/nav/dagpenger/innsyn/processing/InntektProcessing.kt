package no.nav.dagpenger.innsyn.processing

import no.nav.dagpenger.innsyn.data.inntekt.Employer
import no.nav.dagpenger.innsyn.data.inntekt.EmployerSummary
import no.nav.dagpenger.innsyn.data.inntekt.EmploymentPeriode
import no.nav.dagpenger.innsyn.data.inntekt.Income
import no.nav.dagpenger.innsyn.data.inntekt.InntektListe
import no.nav.dagpenger.innsyn.data.inntekt.InntektsInformasjon
import no.nav.dagpenger.innsyn.data.inntekt.MonthIncomeInformation
import no.nav.dagpenger.innsyn.data.objects.Opptjeningsperiode
import no.nav.dagpenger.innsyn.lookup.getNameFromID
import java.time.LocalDate
import java.time.YearMonth
import kotlin.streams.toList

data class ArbeidsgiverOgInntekt(val arbeidsgiver: String, val inntekt: Double)
data class ArbeidsgiverOgPeriode(val arbeidsgiver: String, val perioder: List<EmploymentPeriode>)

fun getInntektForFirstMonth(inntektData: InntektsInformasjon): Double? {
    return inntektData.inntekt.arbeidsInntektMaaned
            .first().arbeidsInntektInformasjon.inntektListe
            .first().beloep
}

fun getInntektForOneMonth(inntektData: InntektsInformasjon, yearMonth: YearMonth): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .first { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned == yearMonth }
            .arbeidsInntektInformasjon.inntektListe
            .sumByDouble { inntektListe -> inntektListe.beloep }
}

fun getPeriodForEachEmployer(inntektData: InntektsInformasjon): List<ArbeidsgiverOgPeriode> {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned in Opptjeningsperiode(LocalDate.now()).get36MonthRange() }
            .flatMap { arbeidsInntektMaaned ->
                arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe
                        .map { inntektListe -> Pair(inntektListe.virksomhet.identifikator, arbeidsInntektMaaned.aarMaaned) }
                        .toList()
            }
            .groupBy { pair -> pair.first }
            .mapValues { element -> element.value.map { pair -> pair.second }.toList() }
            .map { element -> ArbeidsgiverOgPeriode(element.key, groupYearMonthIntoPeriods(element.value)) }
}

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

fun getEmployerSummaries(inntektData: InntektsInformasjon): List<EmployerSummary> {
    return getPeriodForEachEmployer(inntektData)
            .map { periods ->
                EmployerSummary(
                        name = getNameFromID(periods.arbeidsgiver),
                        orgID = periods.arbeidsgiver,
                        employmentPeriodes = periods.perioder,
                        income = getInntektPerArbeidsgiverList(inntektData)
                                .first { arbeidsgiverOgInntekt -> arbeidsgiverOgInntekt.arbeidsgiver == periods.arbeidsgiver }
                                .inntekt
                )
            }
}

fun getInntektForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).get36MonthRange() }
            .sumByDouble { arbeidsInntektMaaned ->
                arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe
                        .sumByDouble { it.beloep }
            }
}

fun getInntektForTheLast12LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).get12MonthRange() }
            .sumByDouble { arbeidsInntektMaaned ->
                arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe
                        .sumByDouble { it.beloep }
            }
}

fun getInntektPerArbeidsgiverList(inntektData: InntektsInformasjon): List<ArbeidsgiverOgInntekt> {
    return inntektData.inntekt.arbeidsInntektMaaned.stream()
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).get36MonthRange() }
            .map { arbeidsInntektMaaned -> arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe }
            .flatMap { inntektListe -> inntektListe.stream() }
            .filter { inntektListe -> inntektListe.header == "Total lønnsinntekt" }
            .map { inntektListe -> ArbeidsgiverOgInntekt(inntektListe.virksomhet.identifikator, inntektListe.beloep) }
            .toList()
}

fun getTotalInntektPerArbeidsgiver(inntektData: InntektsInformasjon): List<ArbeidsgiverOgInntekt> {
    return getInntektPerArbeidsgiverList(inntektData)
            .groupBy { it.arbeidsgiver }
            .mapValues { values ->
                values.value.stream()
                        .map { arbeidsgiverOgInntekt -> arbeidsgiverOgInntekt.inntekt }
                        .reduce { sum, inntekt -> sum + inntekt }
            }
            .mapValues { values -> values.value.get() }
            .map { (arbeidsgiver, inntekt) -> ArbeidsgiverOgInntekt(arbeidsgiver, inntekt) }
            .toList()
}

fun getEmployerMonth(inntektListe: List<InntektListe>): List<Employer> {
    return inntektListe
            .groupBy { inntekt -> inntekt.virksomhet }
            .mapValues { groupedInntekt ->
                groupedInntekt.value
                        .map { inntekt ->
                            Income(
                                    income = inntekt.beloep,
                                    verdikode = inntekt.verdikode
                            )
                        }
            }
            .map { inntekt ->
                Employer(
                        name = getNameFromID(inntekt.key.identifikator),
                        orgID = inntekt.key.identifikator,
                        incomes = inntekt.value

                )
            }
            .toList()
}

fun getMonthsIncomeInformation(inntektData: InntektsInformasjon): List<MonthIncomeInformation> {
    return inntektData.inntekt.arbeidsInntektMaaned.stream()
            .map { arbeidsInntektMaaned ->
                MonthIncomeInformation(
                        arbeidsInntektMaaned.aarMaaned,
                        getEmployerMonth(arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe)
                )
            }
            .toList()
}
