package no.nav.dagpenger.innsyn.data.objects

import no.bekk.bekkopen.date.NorwegianDateUtil
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

class Opptjeningsperiode(beregningsdato: LocalDate) {
    private val antattRapporteringsFrist = LocalDate.of(beregningsdato.year, beregningsdato.month, 5)
    private val reellRapporteringsFrist: LocalDate =
            finnFoersteArbeidsdagEtterRapporterteringsFrist(antattRapporteringsFrist)
    private val maanedSubtraksjon: Long = when {
        beregningsdato.isBefore(reellRapporteringsFrist) || beregningsdato.isEqual(reellRapporteringsFrist) -> 2
        else -> 1
    }

    val sisteAvsluttendeKalenderMaaned: YearMonth = beregningsdato.minusMonths(maanedSubtraksjon).toYearMonth()
    val foersteMaaned: YearMonth = sisteAvsluttendeKalenderMaaned.minusMonths(36)
    val foersteMaaned12: YearMonth = sisteAvsluttendeKalenderMaaned.minusMonths(12)

    private fun finnFoersteArbeidsdagEtterRapporterteringsFrist(rapporteringsFrist: LocalDate): LocalDate {
        return if (rapporteringsFrist.erArbeidsdag()) rapporteringsFrist else finnFoersteArbeidsdagEtterRapporterteringsFrist(
                rapporteringsFrist.plusDays(1)
        )
    }

    fun get36MonthRange(): ClosedRange<YearMonth> = foersteMaaned..sisteAvsluttendeKalenderMaaned

    fun get12MonthRange(): ClosedRange<YearMonth> = foersteMaaned12..sisteAvsluttendeKalenderMaaned

    private fun LocalDate.erArbeidsdag(): Boolean =
            NorwegianDateUtil.isWorkingDay(Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant()))

    private fun LocalDate.toYearMonth(): YearMonth = YearMonth.of(this.year, this.month)
}
