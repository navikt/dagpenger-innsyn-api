package no.nav.dagpenger.innsyn.objects

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.events.inntekt.v1.Aktør
import no.nav.dagpenger.events.inntekt.v1.AktørType
import no.nav.dagpenger.events.inntekt.v1.Avvik
import no.nav.dagpenger.events.inntekt.v1.InntektId
import no.nav.dagpenger.events.inntekt.v1.MånedsInntekt
import no.nav.dagpenger.events.inntekt.v1.Postering
import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.YearMonth

//This class cannot be mocked as it is JSON parsed back and forth to test our parsing
//As such, when extending, do NOT use mock classes

val AUG2018 = YearMonth.of(2018, 8)
val SEP2018 = YearMonth.of(2018, 9)
val MAR2019 = YearMonth.of(2019, 3)
val FUTUREMONTH = YearMonth.now().plusMonths(1)

val TESTSTRING = ""

val WAGESMALL1 = BigDecimal.valueOf(1102.15)
val WAGESMALL2 = BigDecimal.valueOf(881.51)
val WAGESMALL3 = BigDecimal.valueOf(999.99)

val WAGEMEDIUM1 = BigDecimal.valueOf(25001.41)
val WAGEMEDIUM2 = BigDecimal.valueOf(11052.50)
val WAGEMEDIUM3 = BigDecimal.valueOf(32210.00)


val WAGELARGE1 = BigDecimal.valueOf(50012.93)
val WAGELARGE2 = BigDecimal.valueOf(33651.11)
val WAGELARGE3 = BigDecimal.valueOf(48000.40)

val testDataVirksomhet1 = Aktør(
        aktørType = AktørType.ORGANISASJON,
        identifikator = "981566378"
)

val testDataVirksomhet2 = Aktør(
        aktørType = AktørType.ORGANISASJON,
        identifikator = "922332231"
)

val testDataVirksomhet3 = Aktør(
        aktørType = AktørType.ORGANISASJON,
        identifikator = "979312059"
)


val testDataPostering11 = Postering(
        beløp = WAGESMALL1,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet1,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering12 = Postering(
        beløp = WAGEMEDIUM1,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet2,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering13 = Postering(
        beløp = WAGELARGE1,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet3,
        posteringsType = PosteringsType.L_FASTLØNN
)

val testDataPostering21 = Postering(
        beløp = WAGESMALL2,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet1,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering22 = Postering(
        beløp = WAGEMEDIUM2,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet2,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering23 = Postering(
        beløp = WAGELARGE2,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet3,
        posteringsType = PosteringsType.L_FASTLØNN
)

val testDataPostering31 = Postering(
        beløp = WAGESMALL3,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet1,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering32 = Postering(
        beløp = WAGEMEDIUM3,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet2,
        posteringsType = PosteringsType.L_FASTLØNN
)
val testDataPostering33 = Postering(
        beløp = WAGELARGE3,
        fordel = TESTSTRING,
        inntektskilde = TESTSTRING,
        inntektsstatus = TESTSTRING,
        inntektsperiodetype = TESTSTRING,
        utbetaltIMåned = AUG2018,
        virksomhet = testDataVirksomhet3,
        posteringsType = PosteringsType.L_FASTLØNN
)

val testDataPosteringer1 = listOf(
        testDataPostering11,
        testDataPostering12,
        testDataPostering13
)

val testDataPosteringer2 = listOf(
        testDataPostering21,
        testDataPostering22,
        testDataPostering23
)

val testDataPosteringer3 = listOf(
        testDataPostering31,
        testDataPostering32,
        testDataPostering33
)

val testDataAvvikListe1 = listOf<Avvik>()

val testDataAvvikListe2 = listOf<Avvik>()

val testDataAvvikListe3 = listOf<Avvik>()

val testDataMaanedsInntekt1 = MånedsInntekt(
        årMåned = AUG2018,
        avvikListe = testDataAvvikListe1,
        posteringer = testDataPosteringer1
)

val testDataMaanedsInntekt2 = MånedsInntekt(
        årMåned = SEP2018,
        avvikListe = testDataAvvikListe2,
        posteringer = testDataPosteringer2
)

val testDataMaanedsInntekt3 = MånedsInntekt(
        årMåned = MAR2019,
        avvikListe = testDataAvvikListe3,
        posteringer = testDataPosteringer3
)

//The processing should ignore this
val testDataMaanedsInntekt4 = MånedsInntekt(
        årMåned = FUTUREMONTH,
        avvikListe = listOf(),
        posteringer = listOf()
)

val testDataMaanedsInntekter = listOf(
        testDataMaanedsInntekt1,
        testDataMaanedsInntekt2,
        testDataMaanedsInntekt3,
        testDataMaanedsInntekt4
)

val testDataUserAktoer = Aktør(
        aktørType = AktørType.AKTOER_ID,
        identifikator = "14110089213"
)

val testDataInntektId = InntektId(ULID().nextULID())

val testDataSpesifisertInntekt = SpesifisertInntekt(
        inntektId = testDataInntektId,
        månedsInntekter = testDataMaanedsInntekter,
        ident = testDataUserAktoer,
        manueltRedigert = false,
        timestamp = LocalDateTime.now()
)

