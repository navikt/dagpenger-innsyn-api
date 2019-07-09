package data.configuration

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType


private val localProperties = ConfigurationMap(
        mapOf(
                "application.profile" to "LOCAL",
                "application.url" to "/inntekt",
                "application.httpPort" to "8099",
                "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api/enheter/",
                "oppslag.url" to "https://localhost:8090/api",
                "oidc.sts.issuerurl" to "http://localhost/",
                "jwks.url" to "https://localhost",
                "jwks.issuer" to "https://localhost"
        )
)
private val devProperties = ConfigurationMap(
        mapOf(
                "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api/enheter/",
                "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
                "oidc.sts.issuerurl" to "https://security-token-service-t4.nais.preprod.local/",
                "jwks.url" to "https://isso-q.adeo.no:443/isso/oauth2/connect/jwk_uri",
                "jwks.issuer" to "https://isso-q.adeo.no:443/isso/oauth2",
                "application.profile" to "DEV",
                "application.url" to "/inntekt",
                "application.httpPort" to "8099"
        )
)
private val prodProperties = ConfigurationMap(
        mapOf(
                "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api/enheter/",
                "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
                "oidc.sts.issuerurl" to "https://security-token-service.nais.adeo.no/",
                "jwks.url" to "https://isso.adeo.no:443/isso/oauth2/connect/jwk_uri",
                "jwks.issuer" to "https://isso.adeo.no:443/isso/oauth2",
                "application.profile" to "PROD",
                "application.url" to "/inntekt",
                "application.httpPort" to "8099"
        )
)

data class Configuration(
        val application: Application = Application()

) {

    data class Application(
            val profile: Profile = config()[Key("application.profile", stringType)].let { Profile.valueOf(it) },
            val httpPort: Int = config()[Key("application.httpPort", intType)],
            val enhetsregisteretUrl: String = config()[Key("enhetsregisteret.url", stringType)],
            val oppslagUrl: String = config()[Key("oppslag.url", stringType)],
            val oicdStsUrl: String = config()[Key("oidc.sts.issuerurl", stringType)],
            val jwksUrl: String = config()[Key("jwks.url", stringType)],
            val jwksIssuer: String = config()[Key("jwks.issuer", stringType)],
            val name: String = "dagpenger-sommer"
    )
}

enum class Profile {
    LOCAL, DEV, PROD
}

fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
    "dev-sbs" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-sbs" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> {
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
    }
}