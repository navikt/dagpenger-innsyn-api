package no.nav.dagpenger.innsyn.routing

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.DefaultExports
import no.nav.dagpenger.innsyn.monitoring.HealthCheck
import no.nav.dagpenger.innsyn.monitoring.HealthStatus

fun Routing.naischecks(healthChecks: List<HealthCheck>) {
    get("/isAlive") {
        if (healthChecks.all { it.status() == HealthStatus.UP }) {
            call.respond(HttpStatusCode.OK, "OK")
        } else {
            call.response.status(HttpStatusCode.ServiceUnavailable)
        }
    }

    get("/isReady") {
        call.respond(HttpStatusCode.OK, "OK")
    }

    get("/metrics") {
        val collectorRegistry = CollectorRegistry.defaultRegistry
        DefaultExports.initialize()

        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}