package receive

import com.auth0.jwk.JwkProviderBuilder
import data.configuration.Configuration
import data.configuration.testURL
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import restapi.APPLICATION_NAME
import restapi.innsynAPI
import restapi.streams.KafkaInnsynProducer
import restapi.streams.producerConfig
import java.net.URL
import java.util.concurrent.*
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvalidInputTests {

    val noData = """
        {
        }
    """.trimIndent()

    private val lackingData = """
        {
            "personnummer":"",
            "beregningsdato":"",
            "token":""
        }
    """.trimIndent()

    private val partialData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01",
            "token":""
        }
    """.trimIndent()

    private val lackingFieldsData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01"
        }
    """.trimIndent()

    @Test
    fun lackingDataFails() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(lackingData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }

    @Test
    fun noDataFails() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(noData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }

    @Test
    fun lackingFieldsDataFails() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(lackingFieldsData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }
    // TODO: Add in invalid data validation tests
//    @Test
//    fun InvalidDataFails() = testApp{
//        handleRequest (HttpMethod.Post, testURL ) {
//            addHeader(HttpHeaders.ContentType, "application/json")
//            setBody()
//        }.apply {
//            assertTrue(requestHandled)
//            Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
//        }
//    }

    @Test
    fun partialDataFails() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(partialData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }
}

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        (innsynAPI(
                jwkProvider = JwkProviderBuilder(URL(Configuration().application.jwksUrl))
                        .cached(10, 24, TimeUnit.HOURS)
                        .rateLimited(10, 1, TimeUnit.MINUTES)
                        .build()))
    }) { callback() }
}
