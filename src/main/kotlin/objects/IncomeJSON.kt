package objects


data class ArbeidsInntektInformasjon(

        val inntektListe: List<InntektListe>
)

data class InntektId(

        val id: String
)

data class Virksomhet(

        val identifikator: String,
        val aktoerType: String
)

data class Inntekt(

        val arbeidsInntektMaaned: List<ArbeidsInntektMaaned>,
        val ident: Ident,
        val fraDato: String,
        val tilDato: String
)

data class ArbeidsInntektMaaned(

        val aarMaaned: String,
        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class Ident(

        val identifikator: String,
        val aktoerType: String
)

data class Opplysningspliktig(

        val identifikator: String,
        val aktoerType: String
)


data class TotalInntekt(

        val inntektId: InntektId,
        val inntekt: Inntekt,
        val manueltRedigert: Boolean,
        val timestamp: String
)

data class Inntektsmottaker(

        val identifikator: String,
        val aktoerType: String
)

data class InntektListe(

        val inntektType: String,
        val header: String,
        val beloep: String,
        val fordel: String,
        val inntektskilde: String,
        val inntektsperiodetype: String,
        val inntektsstatus: String,
        val leveringstidspunkt: String,
        val utbetaltIMaaned: String,
        val opplysningspliktig: Opplysningspliktig,
        val virksomhet: Virksomhet,
        val inntektsmottaker: Inntektsmottaker,
        val inngaarIGrunnlagForTrekk: Boolean,
        val utloeserArbeidsgiveravgift: Boolean,
        val informasjonsstatus: String,
        val verdikode: String,
        val beskrivelse: String
)

