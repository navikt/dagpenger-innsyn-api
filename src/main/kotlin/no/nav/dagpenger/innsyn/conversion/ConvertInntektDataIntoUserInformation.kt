package no.nav.dagpenger.innsyn.conversion

import mu.KotlinLogging
import no.nav.dagpenger.innsyn.conversion.objects.UserInformation
import no.nav.dagpenger.innsyn.conversion.objects.InntektsInformasjon

private val logger = KotlinLogging.logger {}

fun convertInntektDataIntoUserInformation(inntektsInformasjon: InntektsInformasjon): UserInformation {
    logger.debug("Converting InntektsInformasjon into UserInformation")
    return UserInformation(
            personnummer = inntektsInformasjon.inntektId.id,
            totalIncome36 = getInntektForTheLast36LastMoths(inntektsInformasjon),
            totalIncome12 = getInntektForTheLast12LastMoths(inntektsInformasjon),
            employerSummaries = getEmployerSummaries(inntektsInformasjon),
            monthsIncomeInformation = getMonthsIncomeInformation(inntektsInformasjon)
    )
}