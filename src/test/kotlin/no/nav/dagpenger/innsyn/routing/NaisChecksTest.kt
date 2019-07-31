package no.nav.dagpenger.innsyn.routing

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.monitoring.HealthStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NaisChecksTest {

    @Test
    fun `isReady route returns 200 OK`() {
        withTestApplication(MockApi()) {
            handleRequest(HttpMethod.Get, "/isReady").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `isAlive route returns 200 OK if all HealthChecks are up`() {
        val healthCheck = mockk<HealthCheck>().apply {
            every { this@apply.status() } returns HealthStatus.UP
        }

        withTestApplication(MockApi(
                healthChecks = listOf(healthCheck, healthCheck)
        )) {
            handleRequest(HttpMethod.Get, "/isAlive").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        verify(exactly = 2) {
            healthCheck.status()
        }
    }

    @Test
    fun `isAlive route returns 503 Not Available if a health check is down`() {
        val healthCheck = mockk<HealthCheck>().apply {
            every { this@apply.status() } returns HealthStatus.UP andThen HealthStatus.DOWN
        }

        withTestApplication(MockApi(
                healthChecks = listOf(healthCheck, healthCheck)
        )) {
            handleRequest(HttpMethod.Get, "/isAlive").apply {
                assertEquals(HttpStatusCode.ServiceUnavailable, response.status())
            }
        }

        verify(exactly = 2) {
            healthCheck.status()
        }
    }

    @Test
    fun `The application produces metrics`() {
        withTestApplication(MockApi()) {
            handleRequest(HttpMethod.Get, "/metrics").run {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.contains("jvm_"))
            }
        }
    }
}