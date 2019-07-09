package receive

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import restapi.APPLICATION_NAME
import restapi.Configuration
import restapi.api
import restapi.streams.KafkaInnsynProducer
import restapi.streams.producerConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class InnsynAPILaunchesTest {

    // TODO: Remove this and test that server is runnable another way
    @Test
    fun testRoot() {
        withTestApplication({ api(KafkaInnsynProducer(producerConfig(
                APPLICATION_NAME,
                Configuration().kafka.brokers))) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
