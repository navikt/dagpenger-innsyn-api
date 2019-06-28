package JSONTests

import com.beust.klaxon.Klaxon
import data.objects.TotalInntekt
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

    @Test
    fun JSONParsesTest() {
        println(Paths.get("").toAbsolutePath().toString())
        val jsonFile = Files.newInputStream(Paths.get("src/test/resources/ExpectedJSONResultForUserPeter"
                .replace("/", File.separator)))
        val result = Klaxon()
                .fieldConverter(YearMonthDouble::class, klaxonConverter)
                .parse<TotalInntekt>(InputStreamReader(jsonFile))
        print(result)

    }

    @Test
    fun JSONParsesTest2() {

        println(Paths.get("").toAbsolutePath().toString())
        val jsonFile = Files.newInputStream(Paths.get("src\\test\\resources\\ExpectedJSONResultForUserBob.json".replace("/", File.separator)))

        val result = Klaxon()
                .fieldConverter(YearMonthDouble::class, klaxonConverter)
                .parse<TotalInntekt>(InputStreamReader(jsonFile))
        print(result)
    }

    @Test
    fun JSONParsesToYearMonthTest() {
        val jsonFile = Files.newInputStream(Paths.get("src/test/resources/ExpectedJSONResultForUserPeter"
                .replace("/", File.separator)))
        val result = Klaxon()
                .fieldConverter(YearMonthDouble::class, klaxonConverter)
                .parse<TotalInntekt>(InputStreamReader(jsonFile))

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
        val jsonFile = Files.newInputStream(Paths.get("src/test/resources/ExpectedJSONResultForUserPeter"
                .replace("/", File.separator)))
        val result = Klaxon()
                .fieldConverter(YearMonthDouble::class, klaxonConverter)
                .parse<TotalInntekt>(InputStreamReader(jsonFile))

        assertTrue(result!!.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep == 5.83)
    }

}

