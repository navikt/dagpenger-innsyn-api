package parsing

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Target(AnnotationTarget.FIELD)
annotation class Double

val doubleParser = object : Converter {
    override fun fromJson(jv: JsonValue): Any? {
        if (jv.string != null) {
            return jv.string!!.toDouble()
        } else {
            throw KlaxonException("could not parse Double: ${jv.string}")
        }
    }

    override fun canConvert(cls: Class<*>) = cls == Double::class.java

    override fun toJson(value: Any) = """"$value""""

}

@Target(AnnotationTarget.FIELD)
annotation class YearMonth

val yearMonthParser = object : Converter {
    override fun canConvert(cls: Class<*>) = cls == YearMonth::class.java

    override fun fromJson(jv: JsonValue) =
            if (jv.string != null) {
                YearMonth.parse(jv.string, DateTimeFormatter.ofPattern("yyyy-MM"))
            } else {
                throw KlaxonException("Could not parse YearMonth: ${jv.string}")
            }

    override fun toJson(value: Any) = """"$value""""
}

@Target(AnnotationTarget.FIELD)
annotation class LocalDate

val localDateParser = object : Converter {
    override fun canConvert(cls: Class<*>) = cls == LocalDate::class.java

    override fun fromJson(jv: JsonValue) =
            if (jv.string != null) {
                LocalDate.parse(jv.string, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } else {
                throw KlaxonException("Could not parse LocalDate: ${jv.string}")
            }

    override fun toJson(value: Any) = """"$value""""
}
