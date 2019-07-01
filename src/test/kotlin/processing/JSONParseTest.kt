package processing

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.time.YearMonth


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONParseTestClass {

    @Test
    fun JSONParsesTest() {
        getJSONparsed("Peter")
        getJSONparsed("Bob")
    }

    @Test
    fun JSONParsesToYearMonthTest() {
        val result = getJSONparsed("Peter")

        assertTrue(result!!.inntekt.fraDato == YearMonth.parse("2017-08"))
        assertTrue(result.inntekt.tilDato == YearMonth.parse("2017-08"))
        assertTrue(result.inntekt.arbeidsInntektMaaned[0].aarMaaned == YearMonth.parse("2017-08"))
        assertTrue(result.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].leveringstidspunkt
                == YearMonth.parse("2019-02"))
        assertTrue(result.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].utbetaltIMaaned
                == YearMonth.parse("2018-03"))
    }

    @Test
    fun JSONParsesToDoubleTest() {
        val result = getJSONparsed("Peter")
        assertTrue(result!!.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep == 5.83)
    }

}


fun getJSONparsed(userName: String): InntektsInformasjon {
    val test = InntektsInformasjon()
    return Klaxon()
            .fieldConverter(YearMonthDouble::class, klaxonConverter)
            .parse<InntektsInformasjon>(InputStreamReader(Files
                    .newInputStream(Paths
                            .get(("src%stest%sresources%sExpectedJSONResultForUser%s.requests"
                                    .format(File.separator, File.separator, File.separator, userName))))))
}

