package processing

import data.inntekt.InntektsInformasjon
import data.objects.Opptjeningsperiode
import java.time.LocalDate
import java.time.YearMonth

public class InntektProcessing {

    fun getInntektForFirstMonth(inntektData: InntektsInformasjon): Double? {
        return inntektData.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep
    }

    fun getInntektForOneMointh(inntektData: InntektsInformasjon, yearMonth: YearMonth): Double {
        var inntektForOneMonth = 0.0
        inntektData.inntekt.arbeidsInntektMaaned.filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned.equals(yearMonth) }.forEach {
            it.arbeidsInntektInformasjon.inntektListe.forEach { inntektForOneMonth += it.beloep }
        }
        return inntektForOneMonth
    }


    fun getIncomForTheLast36LastMoths(inntektData: InntektsInformasjon): Double {
        val foersteMaaned = Opptjeningsperiode(LocalDate.now()).foersteMaaned
        val sisteMaaned = Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMaaned
        var inntektFor36months = 0.0
        inntektData.inntekt.arbeidsInntektMaaned.filter { it.aarMaaned in foersteMaaned..sisteMaaned }.forEach {
            it.arbeidsInntektInformasjon.inntektListe.forEach { inntektFor36months += it.beloep }
        }
        return inntektFor36months
    }
}
