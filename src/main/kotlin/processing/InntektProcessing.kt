package processing


import data.inntekt.InntektsInformasjon
import data.objects.Opptjeningsperiode
import java.time.LocalDate
import java.time.YearMonth

fun getInntektForFirstMonth(inntektData: InntektsInformasjon): Double? {
    return inntektData.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep
}

fun getInntektForOneMonth(inntektData: InntektsInformasjon, yearMonth: YearMonth): Double {
    var inntektForOneMonth = 0.0
    inntektData.inntekt.arbeidsInntektMaaned
            .filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned.equals(yearMonth) }
            .forEach {
        it.arbeidsInntektInformasjon.inntektListe
                .forEach { inntektForOneMonth += it.beloep }
    }
    return inntektForOneMonth
}


fun getIncomForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
    val foersteMaaned = Opptjeningsperiode(LocalDate.now()).foersteMaaned
    val sisteMaaned = Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned
    var inntektFor36months = 0.0
    inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in foersteMaaned..sisteMaaned }
            .forEach {
                it.arbeidsInntektInformasjon.inntektListe
                        .forEach { inntektFor36months += it.beloep }
            }
    return inntektFor36months
}


data class ArbeidsgiverOgInntekt(val arbeidsgiver: String, val inntekt: Double)



        fun getInntektPerArbeidsgiverList(inntektData: InntektsInformasjon): ArrayList<ArbeidsgiverOgInntekt> {
            val førsteMaaned = Opptjeningsperiode(LocalDate.now()).foersteMaaned
            val sisteMaaned = Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned
            //val employerSummaries = HashMap<String, Double>()
            val arbeidsgiverOgInntektListe = ArrayList<ArbeidsgiverOgInntekt>()
            if (inntektData == null) {
                throw Exception()
            }

            inntektData.inntekt.arbeidsInntektMaaned.stream()
                    .filter { it.aarMaaned >= førsteMaaned && it.aarMaaned <= sisteMaaned }
                    .forEach {
                        it.arbeidsInntektInformasjon.inntektListe
                                .filter{ inntektListe -> inntektListe.header == "Total lønnsinntekt"}
                                .forEach { arbeidsgiverOgInntektListe.add(ArbeidsgiverOgInntekt(it.virksomhet.identifikator,it.beloep))}

                    }
            return arbeidsgiverOgInntektListe
        }

        fun getTotalInntektPerArbeidsgiver(inntektData: InntektsInformasjon): ArrayList<ArbeidsgiverOgInntekt> {
            val InntektList =  ArrayList<ArbeidsgiverOgInntekt>()
            val incomeandEmployerList = getInntektPerArbeidsgiverList(inntektData)
            incomeandEmployerList
                    .groupBy { it.arbeidsgiver  }
                    .mapValues { values -> values.value.stream()
                            .map { aoi -> aoi.inntekt }
                            .reduce { sum, inntekt -> sum + inntekt }}
                    .mapValues { values -> values.value.get() }
                    .map { (arbeidsgiver,inntekt) ->  InntektList.add(ArbeidsgiverOgInntekt(arbeidsgiver,inntekt))}
            return InntektList

        }






