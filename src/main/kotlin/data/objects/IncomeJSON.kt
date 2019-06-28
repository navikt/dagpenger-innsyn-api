package data.objects

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import parsing.YearMonthDouble
import java.time.YearMonth
import java.time.format.DateTimeParseException

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
        @YearMonthDouble val fraDato: YearMonth,
        @YearMonthDouble val tilDato: YearMonth
)

data class ArbeidsInntektMaaned(

        @YearMonthDouble val aarMaaned: YearMonth,
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

data class TotalInntekt @JvmOverloads constructor(

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
        @YearMonthDouble val beloep: Double,
        val fordel: String,
        val inntektskilde: String,
        val inntektsperiodetype: String,
        val inntektsstatus: String,
        @YearMonthDouble val leveringstidspunkt: YearMonth,
        @YearMonthDouble val utbetaltIMaaned: YearMonth,
        val opplysningspliktig: Opplysningspliktig,
        val virksomhet: Virksomhet,
        val inntektsmottaker: Inntektsmottaker,
        val inngaarIGrunnlagForTrekk: Boolean,
        val utloeserArbeidsgiveravgift: Boolean,
        val informasjonsstatus: String,
        val verdikode: String,
        val beskrivelse: String
)


