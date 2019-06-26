package objects


//data class TotalInntekt(
//        val inntektID: InntektID,
//        val inntekt: Inntekt,
//        val manueltRedigert: Boolean,
//        val timestamp: Date
//)
//
//data class InntektID(
//        val id: String
//)
//
//data class Inntekt(
//        val arbeidsInntektMaaned : ArbeidsInntektMaaned,
//        val ident: Ident,
//        val fraDato: YearMonth,
//        val tilDato: YearMonth
//)
//
//data class ArbeidsInntektMaaned (
//        val maaneder: List<Maaned>
//)
//
//data class Ident(
//        val identifikator: String,
//        val aktoerType: String
//)
//
//data class Maaned(
//        val aarMaaned: YearMonth,
//        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
//)
//
//data class ArbeidsInntektInformasjon(
//        val inntektsListe: InntektListe
//)
//
//data class InntektListe(
//        val inntekter: List<InntektMaaned>
//)
//
//data class InntektMaaned(
//        val inntektsType: String,
//        val header: String,
//        val beloep: Double,
//        val fordel: String,
//        val inntektskilde: String,
//        val inntektsperiodetype: String,
//        val inntektsstatus: String,
//        val leveringstidspunkt: YearMonth,
//        val utbetaltMaaned: YearMonth,
//        val opplysningspliktig: Opplysningpliktig,
//        val virksomhet: Virksomhet,
//        val inntektsmottaker: Inntektsmottaker,
//        val inngaarIGrunnlagForTrekk: Boolean,
//        val utloeserArbeidsgiveravgift: Boolean,
//        val informasjonsstatus: String,
//        val verdikode: String,
//        val beskrivelse: String
//)
//
//data class Opplysningpliktig(
//        val identifikator: String,
//        val aktoerType: String
//)
//
//data class Virksomhet(
//        val identifikator: String,
//        val aktoerType: String
//)
//
//data class Inntektsmottaker(
//        val identifikator: String,
//        val aktoerType: String
//)

data class ArbeidsInntektInformasjon (

        val inntektListe : List<InntektListe>
)

data class InntektId (

        val id : String
)

data class Virksomhet (

        val identifikator : String,
        val aktoerType : String
)

data class Inntekt (

        val arbeidsInntektMaaned : List<ArbeidsInntektMaaned>,
        val ident : Ident,
        val fraDato : String,
        val tilDato : String
)

data class ArbeidsInntektMaaned (

        val aarMaaned : String,
        val arbeidsInntektInformasjon : ArbeidsInntektInformasjon
)

data class Ident (

        val identifikator : String,
        val aktoerType : String
)

data class Opplysningspliktig (

        val identifikator : String,
        val aktoerType : String
)



data class TotalInntekt (

        val inntektId : InntektId,
        val inntekt : Inntekt,
        val manueltRedigert : Boolean,
        val timestamp : String
)

data class Inntektsmottaker (

        val identifikator : String,
        val aktoerType : String
)

data class InntektListe (

        val inntektType : String,
        val header : String,
        val beloep : String,
        val fordel : String,
        val inntektskilde : String,
        val inntektsperiodetype : String,
        val inntektsstatus : String,
        val leveringstidspunkt : String,
        val utbetaltIMaaned : String,
        val opplysningspliktig : Opplysningspliktig,
        val virksomhet : Virksomhet,
        val inntektsmottaker : Inntektsmottaker,
        val inngaarIGrunnlagForTrekk : Boolean,
        val utloeserArbeidsgiveravgift : Boolean,
        val informasjonsstatus : String,
        val verdikode : String,
        val beskrivelse : String
)

