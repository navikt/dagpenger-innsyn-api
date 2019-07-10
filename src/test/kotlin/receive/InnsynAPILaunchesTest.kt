package receive

import com.auth0.jwk.JwkProviderBuilder
import data.configuration.Configuration
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import restapi.APPLICATION_NAME
import restapi.innsynAPI
import restapi.streams.KafkaInnsynProducer
import restapi.streams.producerConfig
import java.net.URL
import java.util.concurrent.*
import kotlin.test.Test
import kotlin.test.assertEquals

class InnsynAPILaunchesTest {

    // TODO: Remove this and test that server is runnable another way
    @Test
    fun testRoot() {
        withTestApplication({
            innsynAPI(KafkaInnsynProducer(producerConfig(APPLICATION_NAME, restapi.Configuration().kafka.brokers)),
                    jwkProvider = JwkProviderBuilder(URL(Configuration().application.jwksUrl))
                            .cached(10, 24, TimeUnit.HOURS)
                            .rateLimited(10, 1, TimeUnit.MINUTES)
                            .build())
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
