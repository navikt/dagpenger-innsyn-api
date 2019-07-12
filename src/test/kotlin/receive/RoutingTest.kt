package receive

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verifyAll
import no.nav.dagpenger.innsyn.restapi.streams.InnsynProducer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoutingTest {

    @Test
    fun `Valid request to inntekt endpoint should succeed and produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val cookie = "ID_token=2416281490ghj; beregningsdato=2019-03-01"

        withTestApplication(MockApi(kafkaMock)) {
            handleRequest(HttpMethod.Get, "/inntekt") {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(requestHandled)
            }
        }

        verifyAll {
            kafkaMock.produceEvent(any())
        }

    }

    @Test
    fun `Request missing cookie should not be accepted and not produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        withTestApplication(MockApi(kafkaMock)) {
            handleRequest(HttpMethod.Get, "/inntekt")
                    .apply { assertEquals(HttpStatusCode.NotAcceptable, response.status()) }
        }

        verifyAll {
            kafkaMock.produceEvent(any()) wasNot Called
        }
    }

    @Test
    fun `Request missing ID_token should not be accepted and not produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val cookie = "beregningsdato=2019-03-01"

        withTestApplication(MockApi(kafkaMock)) {
            handleRequest(HttpMethod.Get, "/inntekt") {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.NotAcceptable, response.status())
            }
        }

        verifyAll {
            kafkaMock.produceEvent(any()) wasNot Called
        }
    }

    @Test
    fun `Request missing beregningsdato should not be accepted and not produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val cookie = "ID_token=2416281490ghj"

        withTestApplication(MockApi(kafkaMock)) {
            handleRequest(HttpMethod.Get, "/inntekt") {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.NotAcceptable, response.status())
            }
        }

        verifyAll {
            kafkaMock.produceEvent(any()) wasNot Called
        }
    }

}