import java.io.File

//fun GetIncome(personnummer: String): Double {
//val JSONForUser = getJSONForUser(personnummer)
//return getIncomeFromJSON(JSONForUser)
//}
fun main() {
    val json = File("ExpectedJSONResultForUserPeter")
            .readText(Charsets.UTF_8)
}