package processing


import data.inntekt.InntektsInformasjon
import data.objects.Opptjeningsperiode
import java.time.LocalDate
import java.time.YearMonth
import kotlin.streams.toList

data class ArbeidsgiverOgInntekt(val arbeidsgiver: String, val inntekt: Double)
data class ArbeidsgiverOgPeriode(val arbeidsgiver: String, val perioder: List<MonthPeriod>)
data class MonthPeriod(val startMonth: YearMonth, val endMonth: YearMonth)

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

fun getPeriodForEachEmployer (inntektData: InntektsInformasjon) : List<ArbeidsgiverOgPeriode>? {
    val test = inntektData.inntekt.arbeidsInntektMaaned
            .filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned in Opptjeningsperiode(LocalDate.now()).foersteMaaned..Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned }
            .map { arbeidsInntektMaaned -> Pair(arbeidsInntektMaaned.aarMaaned, arbeidsInntektMaaned.arbeidsInntektInformasjon.inntektListe.stream()
                    .map { inntektData -> inntektData.virksomhet.identifikator }
                    .toList()) }
            .sortedBy { pair -> pair.first }
            .map { pair -> pair.second.stream()
                    .map { arbeidsgiverID -> Pair(arbeidsgiverID, pair.first) }
                    .toList()}
            .groupBy { pair -> pair.first() }
            .mapValues { groupedPairValues -> groupedPairValues}
    println(test)
    return null

}


fun getInntektForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).foersteMaaned..Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned }
            .sumByDouble {
                it.arbeidsInntektInformasjon.inntektListe
                        .sumByDouble { it.beloep }
            }
}

fun getInntektPerArbeidsgiverList(inntektData: InntektsInformasjon): List<ArbeidsgiverOgInntekt> {
    return inntektData.inntekt.arbeidsInntektMaaned.stream()
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).foersteMaaned..Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned }
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

fun main() {
    getPeriodForEachEmployer()
}






