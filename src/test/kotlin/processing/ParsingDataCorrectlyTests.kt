package processing

import com.beust.klaxon.Klaxon
import data.inntekt.InntektsInformasjon
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parsing.YearMonthDouble
import parsing.klaxonConverter
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
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

}


fun getJSONParsed(userName: String): InntektsInformasjon {
    return Klaxon()
            .fieldConverter(YearMonthDouble::class, klaxonConverter)
            .parse<InntektsInformasjon>(InputStreamReader(Files
                    .newInputStream(Paths
                            .get(("src%stest%sresources%sresults%sjson%sExpectedJSONResultForUser%s.json"
                                    .format(File.separator, File.separator, File.separator, File.separator, File.separator, userName))))))!!
}

