package parsing

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import java.time.YearMonth
import java.time.format.DateTimeParseException

@Target(AnnotationTarget.FIELD)
annotation class YearMonthDouble

val klaxonConverter = object : Converter {
    override fun canConvert(cls: Class<*>) = cls == YearMonth::class.java
            || cls == Double::class.java

    override fun toJson(o: Any)
    // TODO: Fix this
            = """ { "date" : $o } """

    override fun fromJson(jv: JsonValue): Any? = try {
        YearMonth.parse(jv.string)
    } catch (e: DateTimeParseException) {
        try {
            jv.string?.toDouble()
        } catch (e: NumberFormatException) {
            throw KlaxonException("Don't know how to convert ${jv.string}")
        }
    }
}
