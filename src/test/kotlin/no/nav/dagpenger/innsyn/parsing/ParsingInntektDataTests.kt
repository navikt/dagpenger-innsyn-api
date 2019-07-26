package no.nav.dagpenger.innsyn.parsing

import com.squareup.moshi.JsonAdapter
import no.nav.dagpenger.events.moshiInstance
import no.nav.dagpenger.innsyn.AUG2018
import no.nav.dagpenger.innsyn.FUTUREMONTH
import no.nav.dagpenger.innsyn.MAR2019
import no.nav.dagpenger.innsyn.SEP2018
import no.nav.dagpenger.innsyn.WAGELARGE3
import no.nav.dagpenger.innsyn.WAGEMEDIUM2
import no.nav.dagpenger.innsyn.WAGESMALL1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.YearMonth
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONParseTestClass {

    @Test
    fun parseJSONToYearMonthTest() {
        val jsonAdapter: JsonAdapter<YearMonth> = moshiInstance.adapter(YearMonth::class.java)
        val augInJSON = jsonAdapter.toJson(AUG2018)
        assertEquals(AUG2018, jsonAdapter.fromJson(augInJSON))

        val sepInJSON = jsonAdapter.toJson(SEP2018)
        assertEquals(SEP2018, jsonAdapter.fromJson(sepInJSON))


        val marInJSON = jsonAdapter.toJson(MAR2019)
        assertEquals(MAR2019, jsonAdapter.fromJson(marInJSON))


        val futureInJSON = jsonAdapter.toJson(FUTUREMONTH)
        assertEquals(FUTUREMONTH, jsonAdapter.fromJson(futureInJSON))
    }

    @Test
    fun parseJSONToDoubleTest() {
        val jsonAdapter: JsonAdapter<BigDecimal> = moshiInstance.adapter(BigDecimal::class.java)
        val smallInJSON = jsonAdapter.toJson(WAGESMALL1)
        assertEquals(WAGESMALL1, jsonAdapter.fromJson(smallInJSON))

        val mediumInJSON = jsonAdapter.toJson(WAGEMEDIUM2)
        assertEquals(WAGEMEDIUM2, jsonAdapter.fromJson(mediumInJSON))

        val largeInJSON = jsonAdapter.toJson(WAGELARGE3)
        assertEquals(WAGELARGE3, jsonAdapter.fromJson(largeInJSON))

    }
}
