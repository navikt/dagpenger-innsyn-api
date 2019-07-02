package processing

import data.inntekt.InntektsInformasjon
import data.objects.Opptjeningsperiode
import java.time.LocalDate
import java.time.YearMonth

fun getInntektForFirstMonth(inntektData: InntektsInformasjon): Double? {
    return inntektData.inntekt.arbeidsInntektMaaned
            .first().arbeidsInntektInformasjon.inntektListe
            .first().beloep
}

fun getInntektForOneMonth(inntektData: InntektsInformasjon, yearMonth: YearMonth): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter{arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned.equals(yearMonth) }
            .first().arbeidsInntektInformasjon.inntektListe
            .sumByDouble { inntektListe -> inntektListe.beloep }
}


fun getInntektForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
    return inntektData.inntekt.arbeidsInntektMaaned
            .filter { it.aarMaaned in Opptjeningsperiode(LocalDate.now()).foersteMaaned..Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned }
            .sumByDouble { it.arbeidsInntektInformasjon.inntektListe
                    .sumByDouble { it.beloep }
    }
}
