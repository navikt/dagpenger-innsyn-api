package no.nav.dagpenger.innsyn.conversion

import no.nav.dagpenger.innsyn.conversion.objects.UserInformation

import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup

fun convertInntektDataIntoUserInformation(spesifisertInntekt: SpesifisertInntekt, periodeResultat: PeriodeResultat, satsResultat: SatsResultat, kvalifisertResultat: KvalifisertResultat, orgMapping: Map<String, String>): UserInformation {
    val monthsIncomeInformation = getMonthsIncomeInformation(spesifisertInntekt, orgMapping)
    val employerSummaries = getEmployerSummaries(spesifisertInntekt, orgMapping)
    return UserInformation(
            totalIncome36 = monthsIncomeInformation
                    .sumByDouble { it.totalIncomeMonth },
            totalIncome12 = monthsIncomeInformation
                    .filter { it.month in get12MonthRange() }
                    .sumByDouble { it.totalIncomeMonth },
            employerSummaries = employerSummaries,
            oppfyllerMinstekrav = kvalifisertResultat.oppfyllerMinsteinntekt,
            monthsIncomeInformation = monthsIncomeInformation,
            periodeAntalluker = periodeResultat.periodeAntallUker,
            ukeSats = satsResultat.ukesats,
            periodIncome = getIncomeForPeriods(spesifisertInntekt.posteringer)

    )
}

fun getUserInformation(spesifisertInntekt: SpesifisertInntekt, brønnøysundLookup: BrønnøysundLookup, periodeResultat: PeriodeResultat, satsResultat: SatsResultat, kvalifisertResultat: KvalifisertResultat): UserInformation {
    return convertInntektDataIntoUserInformation(spesifisertInntekt, periodeResultat, satsResultat, kvalifisertResultat, getOrgMapping(spesifisertInntekt, brønnøysundLookup))
}

fun getOrgMapping(spesifisertInntekt: SpesifisertInntekt, brønnøysundLookup: BrønnøysundLookup): Map<String, String> {
    return spesifisertInntekt
            .posteringer.map { it.virksomhet!!.identifikator }
            .distinct()
            .associateWith { brønnøysundLookup.getNameFromBrønnøysundRegisterByID(it) }
}