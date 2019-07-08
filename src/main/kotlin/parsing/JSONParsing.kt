package parsing

import data.inntekt.InntektsInformasjon
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

fun getJSONParsed(userName: String): InntektsInformasjon {
    return defaultParser
            .parse<InntektsInformasjon>(InputStreamReader(
                    Files.newInputStream(Paths
                            .get(("src%stest%sresources%sresults%sjson%sExpectedJSONResultForUser%s.json"
                                    .format(File.separator, File.separator, File.separator, File.separator, File.separator, userName))))))!!
}