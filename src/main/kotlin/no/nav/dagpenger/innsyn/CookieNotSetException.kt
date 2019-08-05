package no.nav.dagpenger.innsyn

import java.lang.RuntimeException

class CookieNotSetException(override val message: String?) : RuntimeException(message)
