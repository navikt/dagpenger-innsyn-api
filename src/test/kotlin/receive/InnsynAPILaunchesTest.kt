package receive

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import restapi.innsynAPITestNoSecurity
import kotlin.test.Test
import kotlin.test.assertEquals

class InnsynAPILaunchesTest {

    @Test
    fun testRoot() {
        withTestApplication({ innsynAPITestNoSecurity() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
