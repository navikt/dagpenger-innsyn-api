package receive

import com.beust.klaxon.Klaxon
import data.inntekt.ProcessedRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parsing.defaultParser
import parsing.doubleParser
import parsing.yearMonthParser
import restapi.getExample
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidDataTests {


    val typicalData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01",
            "token":"1234567890ABCDEFghijkl"
        }
    """.trimIndent()

    @Test
    fun testTypicalData() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
            //TODO: Fix this test. Need correct object mappings and repsonses to test
        }
    }

    @Test
    fun testEdgeData() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun testUntypicalData() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun testValidButNotRealData() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(typicalData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.OK, response.status())
        }
    }
}