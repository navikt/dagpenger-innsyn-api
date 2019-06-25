package kotlin

import java.time.LocalDateTime

fun main() {
    println("Hello, world!!!")
}

class Employer {

    var startMonth = Date();
    var endMonth = Date();
    var totalIncome = 0;
    var employment = 0;


}

object Employers {
    val employers = mutableListOf<Employer>();

    fun getSum() : Double {
        return employers
            .sumByDouble(employer -> employer.totalIncome)
    }
}