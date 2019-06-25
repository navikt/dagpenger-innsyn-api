package kotlin

fun GetIncome(personnummer: String): Double {
    val JSONForUser = getJSONForUser(personnummer)
    return getIncomeFromJSON(JSONForUser)
}