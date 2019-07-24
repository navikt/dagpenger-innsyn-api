package no.nav.dagpenger.innsyn.processing

import mu.KotlinLogging
import no.nav.dagpenger.innsyn.data.inntekt.InntektsInformasjon
import no.nav.dagpenger.innsyn.data.inntekt.ProcessedRequest

private val logger = KotlinLogging.logger {}

fun convertInntektDataIntoProcessedRequest(inntektsInformasjon: InntektsInformasjon): ProcessedRequest {
    logger.debug("Converting InntektsInformasjon into ProcessedRequest")
    return ProcessedRequest(
            personnummer = inntektsInformasjon.inntektId.id,
            totalIncome36 = getInntektForTheLast36LastMoths(inntektsInformasjon),
            totalIncome12 = getInntektForTheLast12LastMoths(inntektsInformasjon),
            employerSummaries = getEmployerSummaries(inntektsInformasjon),
            monthsIncomeInformation = getMonthsIncomeInformation(inntektsInformasjon)
    )
}