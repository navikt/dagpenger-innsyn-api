package parsing

import com.beust.klaxon.Klaxon
import data.objects.APITestRequest
import data.objects.OnlyLocalDate
import data.requests.APIPostRequest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.YearMonth

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONParseTestClass {

    private val testDataPeter = getJSONParsed("Peter")

    @Test
    fun JSONParsesToYearMonthTest() {
        assertTrue(testDataPeter.inntekt.fraDato == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.tilDato == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].aarMaaned == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].leveringstidspunkt
                == YearMonth.parse("2019-02"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].utbetaltIMaaned
                == YearMonth.parse("2018-03"))
    }

    @Test
    fun JSONParsesToDoubleTest() {
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep == 5.83)
    }

    @Test
    fun KlaxonParsesLocalDate() {
        Klaxon()
                .fieldConverter(LocalDate::class, localDateParser)
                .parse<APIPostRequest>("""
                    {
                        "personnummer": "15118512351",
                        "beregningsdato": "2019-03-01",
                        "token":"ah82638419gvh123bn"
                    }
                """.trimIndent())
    }

    @Test
    fun KlaxonParsesWithoutLocalDate() {
        Klaxon()
                .parse<APITestRequest>("""
                    {
                        "personnummer": "15118512351",
                        "token":"ah82638419gvh123bn"
                    }
                """.trimIndent())
    }

    @Test
    fun KlaxonParsesBackToLocalDate() {
        Klaxon()
                .fieldConverter(LocalDate::class, localDateParser)
                .parse<OnlyLocalDate>(
                        Klaxon().toJsonString(OnlyLocalDate(java.time.LocalDate.now()))
                )
    }
}
