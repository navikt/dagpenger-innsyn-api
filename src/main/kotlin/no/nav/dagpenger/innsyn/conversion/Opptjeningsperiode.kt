package no.nav.dagpenger.innsyn.conversion

import no.bekk.bekkopen.date.NorwegianDateUtil
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

private val antattRapporteringsFrist = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 5)
private val reellRapporteringsFrist: LocalDate =
        foersteArbeidsdagEtterRapporterteringsFrist(antattRapporteringsFrist)
private val maanedSubtraksjon: Long =
        if (LocalDate.now().isBefore(reellRapporteringsFrist) || LocalDate.now().isEqual(reellRapporteringsFrist)) 2 else 1

val sisteAvsluttendeKalenderMaaned: YearMonth = LocalDate.now().minusMonths(maanedSubtraksjon).toYearMonth()
val foersteMaaned36: YearMonth = sisteAvsluttendeKalenderMaaned.minusMonths(36)
val foersteMaaned12: YearMonth = sisteAvsluttendeKalenderMaaned.minusMonths(12)

private fun foersteArbeidsdagEtterRapporterteringsFrist(rapporteringsFrist: LocalDate): LocalDate =
    if (rapporteringsFrist.erArbeidsdag()) rapporteringsFrist
    else foersteArbeidsdagEtterRapporterteringsFrist(rapporteringsFrist.plusDays(1))

private fun LocalDate.erArbeidsdag(): Boolean =
        NorwegianDateUtil.isWorkingDay(Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant()))

private fun LocalDate.toYearMonth(): YearMonth = YearMonth.of(this.year, this.month)

fun get36MonthRange(): ClosedRange<YearMonth> = foersteMaaned36..sisteAvsluttendeKalenderMaaned

fun get12MonthRange(): ClosedRange<YearMonth> = foersteMaaned12..sisteAvsluttendeKalenderMaaned
