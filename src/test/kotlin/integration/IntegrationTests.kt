package integration

import no.nav.dagpenger.innsyn.parsing.getJSONParsed
import no.nav.dagpenger.innsyn.processing.convertInntektDataIntoProcessedRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTests {

    // TODO: Add tests here that actually check that things work
    @Test
    fun canConvertInntektDataIntoProcessedRequest() {
        println(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel")))
    }
}