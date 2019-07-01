package data.objects


import no.bekk.bekkopen.date.NorwegianDateUtil
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

data class Opptjeningsperiode(val beregningsdato: LocalDate) {
    private val antattRapporteringsFrist = LocalDate.of(beregningsdato.year, beregningsdato.month, 5)
    private val reellRapporteringsFrist: LocalDate =
            finnFoersteArbeidsdagEtterRapporterteringsFrist(antattRapporteringsFrist)
    private val maanedSubtraksjon: Long = when {
        beregningsdato.isBefore(reellRapporteringsFrist) || beregningsdato.isEqual(reellRapporteringsFrist) -> 2
        else -> 1
    }

    val sisteAvsluttendeKalenderMaaned: YearMonth = beregningsdato.minusMonths(maanedSubtraksjon).toYearMonth()
    val foersteMaaned: YearMonth = sisteAvsluttendeKalenderMaaned.minusMonths(36)

    fun sammeOpptjeningsPeriode(other: Opptjeningsperiode): Boolean =
            this.sisteAvsluttendeKalenderMaaned == other.sisteAvsluttendeKalenderMaaned

    private fun finnFoersteArbeidsdagEtterRapporterteringsFrist(rapporteringsFrist: LocalDate): LocalDate {
        return if (rapporteringsFrist.erArbeidsdag()) rapporteringsFrist else finnFoersteArbeidsdagEtterRapporterteringsFrist(
                rapporteringsFrist.plusDays(1)
        )
    }

    private fun LocalDate.erArbeidsdag(): Boolean =
            NorwegianDateUtil.isWorkingDay(Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant()))

    private fun LocalDate.toYearMonth(): YearMonth = YearMonth.of(this.year, this.month)
}

