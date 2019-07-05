package integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parsing.getJSONParsed
import processing.convertInntektDataIntoProcessedRequest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTests {


    @Test
    fun canConvertInntektDataIntoProcessedRequest() {
        println(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel")))
    }
}