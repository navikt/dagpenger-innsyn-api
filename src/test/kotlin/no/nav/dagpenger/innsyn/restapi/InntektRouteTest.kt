package no.nav.dagpenger.innsyn.restapi

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
import mu.KotlinLogging
import no.nav.dagpenger.innsyn.JwtStub
import no.nav.dagpenger.innsyn.lookup.AktoerRegisterLookup
import no.nav.dagpenger.innsyn.lookup.BrønnøysundLookup
import no.nav.dagpenger.innsyn.lookup.InnsynProducer
import no.nav.dagpenger.innsyn.lookup.objects.PacketStore
import no.nav.dagpenger.innsyn.settings.Configuration
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InntektRouteTest {

    companion object {
        private val DOCKER_PATH = Paths.get("aktoer-mock/")

        class KGenericContainer : GenericContainer<KGenericContainer>(ImageFromDockerfile()
                .withFileFromPath(".", DOCKER_PATH)
                .withDockerfilePath("./Dockerfile.ci"))

        @ClassRule
        val mockContainer = KGenericContainer()
    }

    private val fakeWait: WaitStrategy = object : WaitStrategy {
        override fun waitUntilReady(waitStrategyTarget: WaitStrategyTarget?) {
            logger.info("Not waiting")
        }

        override fun withStartupTimeout(startupTimeout: Duration?): WaitStrategy {
            return this
        }
    }

    private val logger = KotlinLogging.logger { }

    private val aktoerRegister: AktoerRegisterLookup
    private val brønnøysundLookup: BrønnøysundLookup
    private val config = Configuration()
    private val jwtStub = JwtStub(config.application.jwksIssuer)

    private val token = jwtStub.createTokenFor(config.application.oidcUser)

    init {
        println("Before Exposed Service")
        println(Paths.get("").toAbsolutePath())
        mockContainer
                .withExposedPorts(3050)
                .waitingFor(fakeWait)
        println("Service exposed")
        mockContainer.start()
        println("Service started")
        while (!mockContainer.isRunning) {
            logger.info("Still waiting")
            Thread.sleep(500)
        }
        println("Done waiting")

        val aktørURL = "http://" +
                mockContainer.containerIpAddress +
                ":" +
                mockContainer.getMappedPort(3050) +
                "/aktoerregister/api/v1/identer"
        val brURL = "http://" +
                mockContainer.containerIpAddress +
                ":" +
                mockContainer.getMappedPort(3050) +
                "/br/"

        println(aktørURL)

        println(mockContainer.containerIpAddress)
        println(mockContainer.getMappedPort(3050))

        this.aktoerRegister = AktoerRegisterLookup(url = aktørURL)
        this.brønnøysundLookup = BrønnøysundLookup(url =)
    }

    @Test
    fun `Valid request to inntekt endpoint should succeed and produce an event to Kafka`() {

        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns true
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                jwkProvider = jwtStub.stubbedJwkProvider(),
                aktoerRegisterLookup = aktoerRegister)
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
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
    fun `504 response on timeout`() {

        val kafkaMock = mockk<InnsynProducer>(relaxed = true)

        val slot = slot<String>()

        val storeMock = mockk<PacketStore>(relaxed = true).apply {
            every { this@apply.isDone(capture(slot)) } returns false
        }

        val cookie = "ID_token=$token"

        withTestApplication(MockApi(
                kafkaProducer = kafkaMock,
                packetStore = storeMock,
                jwkProvider = jwtStub.stubbedJwkProvider(),
                aktoerRegisterLookup = aktoerRegister)
        ) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply {
                assertEquals(HttpStatusCode.GatewayTimeout, response.status())
                assertTrue(requestHandled)
            }
        }

        verifyAll {
            storeMock.get(slot.toString()) wasNot Called
        }
    }

    @Test
    fun `Request missing ID token should be unauthorized`() {
        withTestApplication(MockApi(
                jwkProvider = jwtStub.stubbedJwkProvider())) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl)
                    .apply { assertEquals(HttpStatusCode.Unauthorized, response.status()) }
        }
    }

    @Test
    fun `Request with invalid ID token should be unauthorized`() {
        val anotherIssuer = JwtStub("https://anotherissuer")
        val cookie = "ID_token=${anotherIssuer.createTokenFor("user")}"

        withTestApplication(MockApi(
                jwkProvider = jwtStub.stubbedJwkProvider())) {
            handleRequest(HttpMethod.Get, config.application.applicationUrl) {
                addHeader(HttpHeaders.Cookie, cookie)
            }.apply { assertEquals(HttpStatusCode.Unauthorized, response.status()) }
        }
    }
}
