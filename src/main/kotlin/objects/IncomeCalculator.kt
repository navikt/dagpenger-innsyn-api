package objects

import com.beust.klaxon.Klaxon
import java.awt.Stroke
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.lang.Exception as Exception2
import kotlin.Exception as Exception1

class IncomeCalculator {
    // property (data member)
    private var name: String = "Sher"

    // member function
    fun printMe() {
        println("You are at the best "+name)
    }
    fun readJSONFile(path : String): TotalInntekt? {
        val jsonFile = Files.newInputStream(Paths.get(path))
        val result = Klaxon()
                .fieldConverter(YearMonthDouble::class, klaxonConverter)
                .parse<TotalInntekt>(InputStreamReader(jsonFile))
        if (result == null) {
            throw kotlin.Exception("No Data")
        }
        return result

    }

    fun getIncomeForFirstMonth(identifikator: String): Double? {

        val result = readJSONFile("src\\test\\resources\\ExpectedJSONResultForUserPeter")
        val incomeData = result?.inntekt?.arbeidsInntektMaaned?.get(0)?.arbeidsInntektInformasjon?.inntektListe?.get(0)?.beloep
        println(incomeData)
        return incomeData
    }

    fun getIncomeForOneMointh(yearMonth: YearMonth): Double {
        val result = readJSONFile("src\\test\\resources\\ExpectedJSONResultForUserBob.json")
        var incomeForOneMotnh =0.0
        if (result != null) {
            result.inntekt.arbeidsInntektMaaned.filter { arbeidsInntektMaaned -> arbeidsInntektMaaned.aarMaaned.equals(yearMonth)}.forEach{
                it.arbeidsInntektInformasjon.inntektListe.forEach{incomeForOneMotnh += it.beloep }
            }


        }

        return incomeForOneMotnh
    }

    fun getIncomForTheLast36LastMoths(): Double{
        val result = readJSONFile("src\\test\\resources\\ExpectedJSONResultForUserBob.json")

        val førsteMaaned = Opptjeningsperiode(LocalDate.now()).førsteMåned
        val sisteMaaned = Opptjeningsperiode(LocalDate.now()).sisteAvsluttendeKalenderMåned
        var incomeFor36months =0.0
        if (result != null) {
            result.inntekt.arbeidsInntektMaaned.filter {it.aarMaaned >= førsteMaaned && it.aarMaaned <= sisteMaaned }.forEach{
                it.arbeidsInntektInformasjon.inntektListe.forEach{incomeFor36months += it.beloep }
            }
        }
        return incomeFor36months
    }





}

fun main(args: Array<String>) {
    val obj = IncomeCalculator() // create obj object of myClass class
    obj.printMe()
    val income = obj.getIncomeForFirstMonth("99999999999")
}
