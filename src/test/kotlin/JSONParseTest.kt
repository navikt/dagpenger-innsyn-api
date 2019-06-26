import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import objects.TotalInntekt

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONParseTestClass{

    @Test
    fun JSONParsesTest () {
        println(Paths.get("").toAbsolutePath().toString())
        val path = Paths.get("").toAbsolutePath().toString()
        val jsonFile = Files.newInputStream(Paths.get("src\\test\\resources\\ExpectedJSONResultForUserPeter"))
        val result = Klaxon()
                .parse<TotalInntekt>(InputStreamReader(jsonFile))
        print(result)

    }

}

