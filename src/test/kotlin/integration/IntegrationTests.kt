package integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parsing.getJSONParsed
import processing.convertInntektDataIntoProcessedRequest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTests {


    //TODO: Add tests here that actually check that things work
    @Test
    fun canConvertInntektDataIntoProcessedRequest() {
        println(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel")))
    }
}