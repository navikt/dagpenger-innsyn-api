package receive

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import no.nav.dagpenger.innsyn.lookup.InnsynProducer
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CookieTest {

    @Test
    fun `Valid request to inntekt endpoint should succeed and produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns true
        }

        val cookie = "nav-esso=2416281490ghj; beregningsdato=2019-06-01"

        withTestApplication(MockApi(kafkaProducer = kafkaMock, packetStore = storeMock)) {
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

        withTestApplication(MockApi(kafkaProducer = kafkaMock)) {
            handleRequest(HttpMethod.Get, "/inntekt")
                    .apply { assertEquals(HttpStatusCode.NotAcceptable, response.status()) }
        }

        verifyAll {
            kafkaMock.produceEvent(any()) wasNot Called
        }
    }

    @Test
    fun `Request missing ID token should not be accepted and not produce an event to Kafka`() {
        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val cookie = "beregningsdato=2019-06-01"

        withTestApplication(MockApi(kafkaProducer = kafkaMock)) {
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

        val cookie = "nav-esso=2416281490ghj"

        withTestApplication(MockApi(kafkaProducer = kafkaMock)) {
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
