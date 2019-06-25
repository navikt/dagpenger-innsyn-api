package kotlin

import java.time.LocalDateTime

fun main() {
    println("Hello, world!!!")
}


fun getSum(employers: MutableList<Employer>) : Double {
    return employers
        .sumByDouble{employer -> employer.totalIncome}
}
