package receive

import data.configuration.testURL
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import restapi.streams.KafkaInnsynProducer
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidDataTests {

    private val typicalData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01",
            "token":"1234567890ABCDEFghijkl"
        }
    """.trimIndent()

    @Test
    fun testTypicalData() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            // TODO: Fix this test. Need correct object mappings and repsonses to test
        }
    }

    // TODO: Fix this test
    @Test
    fun testEdgeData() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    // TODO: Fix this test
    @Test
    fun testUntypicalData() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    // TODO: Fix this test
    @Test
    fun testValidButNotRealData() = testApp {
        handleRequest(HttpMethod.Post, testURL) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    fun testApp(callback: TestApplicationEngine.() -> Unit) {
        val kafkaMock = mockk<KafkaInnsynProducer>(relaxed = true)

        withTestApplication(
                MockApi(kafkaMock)
        ) { callback() }
    }
}