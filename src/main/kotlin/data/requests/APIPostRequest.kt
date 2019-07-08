package data.requests

import parsing.LocalDate

data class APIPostRequest(
        val personnummer: String,
        @LocalDate val beregningsdato: java.time.LocalDate,
        val token: String
)
