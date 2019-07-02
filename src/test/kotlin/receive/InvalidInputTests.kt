package receive

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
import restapi.innsynAPI
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class invalidInputTests {

    val noData = """
        {
        }
    """.trimIndent()

    val lackingData = """
        {
            "personnummer":"",
            "beregningsdato":"",
            "token":""
        }
    """.trimIndent()

    val partialData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01",
            "token":"ah82638419gvh123bn"
        }
    """.trimIndent()

    val lackingFieldsData = """
        {
            "personnummer": "15118512351",
            "beregningsdato": "2019-03-01"
        }
    """.trimIndent()

    @Test
    fun LackingDataFails() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(lackingData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }

    @Test
    fun NoDataFails() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(noData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }

    @Test
    fun LackingFieldsDataFails() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(lackingFieldsData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }
    //TODO: Add in invalid data validation tests
//    @Test
//    fun InvalidDataFails() = testApp{
//        handleRequest (HttpMethod.Post, "/inntekt" ) {
//            addHeader(HttpHeaders.ContentType, "application/json")
//            setBody()
//        }.apply {
//            assertTrue(requestHandled)
//            Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
//        }
//    }

    @Test
    fun PartialDataFails() = testApp {
        handleRequest(HttpMethod.Post, "/inntekt") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(partialData)
        }.apply {
            assertTrue(requestHandled)
            Assertions.assertEquals(HttpStatusCode.NotAcceptable, response.status())
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            (innsynAPI())
        }) { callback() }
    }
}