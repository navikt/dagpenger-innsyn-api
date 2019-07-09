package processing

import data.inntekt.EmployerSummary
import data.inntekt.Employer
import data.inntekt.EmploymentPeriode
import data.inntekt.InntektListe
import data.inntekt.InntektsInformasjon
import data.inntekt.Income
import data.inntekt.MonthIncomeInformation
import data.objects.Opptjeningsperiode
import lookup.getNameFromID
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
            .filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned.equals(yearMonth) }
            .first().arbeidsInntektInformasjon.inntektListe
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
                if (list.isEmpty()) list + EmploymentPeriode(yearMonth, yearMonth)
                else if (list.last().endDateYearMonth.plusMonths(1).equals(yearMonth))
                        list.dropLast(1) + EmploymentPeriode(list.last().startDateYearMonth, yearMonth)
                    else list + EmploymentPeriode(yearMonth, yearMonth)
            })
}

fun getEmployerSummaries(inntektData: InntektsInformasjon): List<EmployerSummary> {
    return getPeriodForEachEmployer(inntektData)
            .map { periods ->
                EmployerSummary(
                        name = getNameFromID(periods.arbeidsgiver),
                        orgID = periods.arbeidsgiver,
                        employmentPeriodes = periods.perioder,
                        income = getInntektPerArbeidsgiverList(inntektData)
                                .filter { arbeidsgiverOgInntekt -> arbeidsgiverOgInntekt.arbeidsgiver.equals(periods.arbeidsgiver) }
                                .first().inntekt
                )
            }
}

fun getInntektForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).get36MonthRange() }
            .sumByDouble {
                it.arbeidsInntektInformasjon.inntektListe
                        .sumByDouble { it.beloep }
            }
}

fun getInntektForTheLast12LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).get12MonthRange() }
            .sumByDouble {
                it.arbeidsInntektInformasjon.inntektListe
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
                        name = inntekt.key.aktoerType,
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
