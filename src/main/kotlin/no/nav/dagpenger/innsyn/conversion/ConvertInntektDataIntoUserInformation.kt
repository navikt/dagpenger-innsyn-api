package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.innsyn.conversion.objects.UserInformation

import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt

fun convertInntektDataIntoUserInformation(spesifisertInntekt: SpesifisertInntekt): UserInformation {
    val monthsIncomeInformation = getMonthsIncomeInformation(spesifisertInntekt)
    return UserInformation(
            personnummer = spesifisertInntekt.ident.identifikator,
            totalIncome36 = monthsIncomeInformation
                    .sumByDouble { it.totalIncomeMonth },
            totalIncome12 = monthsIncomeInformation
                    .filter { it.month in get12MonthRange() }
                    .sumByDouble { it.totalIncomeMonth },
            employerSummaries = getEmployerSummaries(spesifisertInntekt),
            monthsIncomeInformation = monthsIncomeInformation
    )
}