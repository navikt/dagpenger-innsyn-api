package no.nav.dagpenger.innsyn.monitoring

interface HealthCheck {
    fun status(): HealthStatus
}

enum class HealthStatus {
    UP, DOWN
}