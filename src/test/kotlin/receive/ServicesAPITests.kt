package receive

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import restapi.innsynAPI
import kotlin.test.Test
import kotlin.test.assertEquals

class ServicesAPITests {

    //TODO: Remove this and test that server is runnable another way
    @Test
    fun testRoot() {
        withTestApplication({ innsynAPI() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
