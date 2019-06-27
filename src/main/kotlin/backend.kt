import objects.Employer

fun main() {
    println("Hello, world!!!")
}


fun getSum(employers: MutableList<Employer>): Double {
    return employers
            .sumByDouble { employer -> employer.totalIncome }
}
