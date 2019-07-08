package receive

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import restapi.APPLICATION_NAME
import restapi.api
import restapi.streams.*
import kotlin.test.Test
import kotlin.test.assertEquals

class InnsynAPILaunchesTest {

    //TODO: Remove this and test that server is runnable another way
    @Test
    fun testRoot() {
        withTestApplication({ api(KafkaInnsynProducer(
                producerConfig(APPLICATION_NAME, "localhost:9092")),
                KafkaInntektConsumer(
                        consumerConfig(APPLICATION_NAME, "localhost:9092"))) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
