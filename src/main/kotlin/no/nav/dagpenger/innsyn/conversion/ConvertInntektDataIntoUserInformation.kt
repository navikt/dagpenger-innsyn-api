package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.innsyn.conversion.objects.UserInformation

import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup

fun convertInntektDataIntoUserInformation(spesifisertInntekt: SpesifisertInntekt, orgMapping: Map<String, String>): UserInformation {
    val monthsIncomeInformation = getMonthsIncomeInformation(spesifisertInntekt, orgMapping)
    val employerSummaries = getEmployerSummaries(spesifisertInntekt, orgMapping)
    return UserInformation(
            personnummer = spesifisertInntekt.ident.identifikator,
            totalIncome36 = monthsIncomeInformation
                    .sumByDouble { it.totalIncomeMonth },
            totalIncome12 = monthsIncomeInformation
                    .filter { it.month in get12MonthRange() }
                    .sumByDouble { it.totalIncomeMonth },
            employerSummaries = employerSummaries,
            monthsIncomeInformation = monthsIncomeInformation
    )
}

fun getUserInformation(spesifisertInntekt: SpesifisertInntekt, brønnøysundLookup: BrønnøysundLookup): UserInformation {
    return convertInntektDataIntoUserInformation(spesifisertInntekt, getOrgMapping(spesifisertInntekt, brønnøysundLookup))
}

fun getOrgMapping(spesifisertInntekt: SpesifisertInntekt, brønnøysundLookup: BrønnøysundLookup): Map<String, String> {
    return spesifisertInntekt
            .posteringer.map { it.virksomhet!!.identifikator }
            .distinct()
            .associateWith { brønnøysundLookup.getNameFromBrønnøysundRegisterByID(it) }
}