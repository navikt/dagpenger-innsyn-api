package no.nav.dagpenger.innsyn.data.inntekt

import java.time.YearMonth

data class InntektsInformasjon(

        val inntektId: InntektId,
        val inntekt: Inntekt,
        val manueltRedigert: Boolean,
        val timestamp: String
)

data class InntektId(

        val id: String
)

data class Inntekt(

        val arbeidsInntektMaaned: List<ArbeidsInntektMaaned>,
        val ident: Ident,
        @no.nav.dagpenger.innsyn.parsing.YearMonth val fraDato: YearMonth,
        @no.nav.dagpenger.innsyn.parsing.YearMonth val tilDato: YearMonth
)

data class ArbeidsInntektMaaned(

        @no.nav.dagpenger.innsyn.parsing.YearMonth val aarMaaned: YearMonth,
        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class ArbeidsInntektInformasjon(

        val inntektListe: List<InntektListe>
)

data class InntektListe(

        val inntektType: String,
        val header: String,
        @no.nav.dagpenger.innsyn.parsing.Double val beloep: Double,
        val fordel: String,
        val inntektskilde: String,
        val inntektsperiodetype: String,
        val inntektsstatus: String,
        @no.nav.dagpenger.innsyn.parsing.YearMonth val leveringstidspunkt: YearMonth,
        @no.nav.dagpenger.innsyn.parsing.YearMonth val utbetaltIMaaned: YearMonth,
        val opplysningspliktig: Opplysningspliktig,
        val virksomhet: Virksomhet,
        val inntektsmottaker: Inntektsmottaker,
        val inngaarIGrunnlagForTrekk: Boolean,
        val utloeserArbeidsgiveravgift: Boolean,
        val informasjonsstatus: String,
        val verdikode: String,
        val beskrivelse: String
)

data class Opplysningspliktig(

        val identifikator: String,
        val aktoerType: String
)

data class Virksomhet(

        val identifikator: String,
        val aktoerType: String
)

data class Inntektsmottaker(

        val identifikator: String,
        val aktoerType: String
)

data class Ident(

        val identifikator: String,
        val aktoerType: String
)