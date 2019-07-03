package data.objects

import parsing.LocalDate

data class APITestRequest(
        val personnummer: String,
        val token: String
)

data class OnlyLocalDate (
        @LocalDate val localDate: java.time.LocalDate
)