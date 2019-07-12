package no.nav.dagpenger.innsyn.processing

import no.nav.dagpenger.innsyn.data.inntekt.InntektsInformasjon
import no.nav.dagpenger.innsyn.data.inntekt.ProcessedRequest

fun convertInntektDataIntoProcessedRequest(inntektsInformasjon: InntektsInformasjon): ProcessedRequest {
    return ProcessedRequest(
            personnummer = inntektsInformasjon.inntektId.id,
            totalIncome = getInntektForTheLast36LastMoths(inntektsInformasjon),
            totalIncome12 = getInntektForTheLast12LastMoths(inntektsInformasjon),
            employerSummaries = getEmployerSummaries(inntektsInformasjon),
            monthsIncomeInformation = getMonthsIncomeInformation(inntektsInformasjon)
    )
}