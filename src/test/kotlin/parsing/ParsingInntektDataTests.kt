package parsing

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.YearMonth

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONParseTestClass {

    private val testDataPeter = getJSONParsed("Peter")

    @Test
    fun parseJSONToYearMonthTest() {
        assertTrue(testDataPeter.inntekt.fraDato == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.tilDato == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].aarMaaned == YearMonth.parse("2017-08"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].leveringstidspunkt
                == YearMonth.parse("2019-02"))
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].utbetaltIMaaned
                == YearMonth.parse("2018-03"))
    }

    @Test
    fun parseJSONToDoubleTest() {
        assertTrue(testDataPeter.inntekt.arbeidsInntektMaaned[0].arbeidsInntektInformasjon.inntektListe[0].beloep == 5.83)
    }
}
