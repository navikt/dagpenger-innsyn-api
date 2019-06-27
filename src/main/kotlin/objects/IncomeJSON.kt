package objects

import java.time.YearMonth
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Converter
import com.beust.klaxon.KlaxonException
import java.time.format.DateTimeParseException

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
        @YearMonthDouble val fraDato : YearMonth,
        @YearMonthDouble val tilDato : YearMonth
)

data class ArbeidsInntektMaaned (

        @YearMonthDouble val aarMaaned : YearMonth,
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



data class TotalInntekt @JvmOverloads constructor(

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
        @YearMonthDouble val beloep : Double,
        val fordel : String,
        val inntektskilde : String,
        val inntektsperiodetype : String,
        val inntektsstatus : String,
        @YearMonthDouble val leveringstidspunkt : YearMonth,
        @YearMonthDouble val utbetaltIMaaned : YearMonth,
        val opplysningspliktig : Opplysningspliktig,
        val virksomhet : Virksomhet,
        val inntektsmottaker : Inntektsmottaker,
        val inngaarIGrunnlagForTrekk : Boolean,
        val utloeserArbeidsgiveravgift : Boolean,
        val informasjonsstatus : String,
        val verdikode : String,
        val beskrivelse : String
)

@Target(AnnotationTarget.FIELD)
annotation class YearMonthDouble

val klaxonConverter = object: Converter {
    override fun canConvert(cls: Class<*>)
            = cls == YearMonth::class.java
            || cls == Double::class.java

    override fun toJson(o: Any)
    // TODO: Fix this
            = """ { "date" : $o } """

    override fun fromJson(jv: JsonValue): Any?
            = try {
        YearMonth.parse(jv.string)
    } catch (e: DateTimeParseException) {
        try {
            jv.string?.toDouble()
        }
        catch (e: NumberFormatException) {
            throw KlaxonException("Don't know how to convert ${jv.string}")
        }
    }
}

