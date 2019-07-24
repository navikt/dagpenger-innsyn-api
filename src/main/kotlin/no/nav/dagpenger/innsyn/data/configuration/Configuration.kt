package no.nav.dagpenger.innsyn.data.configuration

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.dagpenger.streams.KafkaCredential

private val localProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/dev/",
        "srvdp.inntekt.innsyn.username" to "igroup",
        "srvdp.inntekt.innsyn.password" to "itest",
        "enhetsregisteret.url" to "https://data.brreg.no/enhetsregisteret/api/enheter/",
        "aktoerregisteret.url" to "http://backend.myapp.com:9011",
        "oppslag.url" to "https://localhost:8090/api",
        "oidc.sts.issuerurl" to "http://localhost/",
        "kafka.bootstrap.servers" to "localhost:9092",
        "jwks.url" to "http://host.docker.internal:4352/certs",
        "jwks.issuer" to "http://simple-oidc-provider",
        "application.profile" to "LOCAL",
        "application.url" to "/inntekt",
        "application.httpPort" to "8099",
        "SRVDP.INNTEKT.INNSYN.USERNAME" to "igroup",
        "srvdp.inntekt.innsyn.password" to "itest"
    )
)

private val devProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/preprod-fss/",
        "enhetsregisteret.url" to "https://no.nav.dagpenger.innsyn.data.brreg.no/enhetsregisteret/api/enheter/",
        "aktoerregisteret.url" to "http://tjenester.nav.no/aktoerregister/api/v1/identer?identgruppe=AktoerId",
        "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
        "oidc.sts.issuerurl" to "https://security-token-service-t4.nais.preprod.local/",
        "kafka.bootstrap.servers" to "b27apvl00045.preprod.local:8443,b27apvl00046.preprod.local:8443,b27apvl00047.preprod.local:8443",
        "jwks.url" to "https://isso-q.adeo.no:443/isso/oauth2/connect/jwk_uri",
        "jwks.issuer" to "https://isso-q.adeo.no:443/isso/oauth2",
        "application.profile" to "DEV",
        "application.url" to "/inntekt",
        "application.httpPort" to "8099"
    )
)
private val prodProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/prod-fss/",
        "enhetsregisteret.url" to "https://no.nav.dagpenger.innsyn.data.brreg.no/enhetsregisteret/api/enheter/",
        "aktoerregisteret.url" to "http://tjenester.nav.no/aktoerregister/api/v1/identer?identgruppe=AktoerId",
        "oppslag.url" to "http://dagpenger-oppslag.default.svc.nais.local/api",
        "oidc.sts.issuerurl" to "https://security-token-service.nais.adeo.no/",
        "kafka.bootstrap.servers" to "a01apvl00145.adeo.no:8443,a01apvl00146.adeo.no:8443,a01apvl00147.adeo.no:8443,a01apvl00148.adeo.no:8443,a01apvl00149.adeo.no:8443,a01apvl150.adeo.no:8443",
        "jwks.url" to "https://isso.adeo.no:443/isso/oauth2/connect/jwk_uri",
        "jwks.issuer" to "https://isso.adeo.no:443/isso/oauth2",
        "application.profile" to "PROD",
        "application.url" to "/inntekt",
        "application.httpPort" to "8099"
    )
)

data class Configuration(
    val application: Application = Application(),
    val vault: Vault = Vault(),
    val kafka: Kafka = Kafka()

) {

    data class Application(
        val profile: Profile = config()[Key("application.profile", stringType)].let { Profile.valueOf(it) },
        val httpPort: Int = config()[Key("application.httpPort", intType)],
        val applicationUrl: String = config()[Key("application.url", stringType)],
        val enhetsregisteretUrl: String = config()[Key("enhetsregisteret.url", stringType)],
        val aktoerregisteretUrl: String = config()[Key("aktoerregisteret.url", stringType)],
        val oppslagUrl: String = config()[Key("oppslag.url", stringType)],
        val oicdStsUrl: String = config()[Key("oidc.sts.issuerurl", stringType)],
        val jwksUrl: String = config()[Key("jwks.url", stringType)],
        val jwksIssuer: String = config()[Key("jwks.issuer", stringType)],
        val name: String = "dagpenger-innsyn-api"
    )

    data class Vault(
        val mountPath: String = config()[Key("vault.mountpath", stringType)]
    )

    data class Kafka(
        val brokers: String = config()[Key("kafka.bootstrap.servers", stringType)],
        val user: String = config()[Key("srvdp.inntekt.innsyn.username", stringType)],
        val password: String = config()[Key("srvdp.inntekt.innsyn.password", stringType)]
    ) {
        fun credential(): KafkaCredential? {
            return if (user != null && password != null) {
                KafkaCredential(user, password)
            } else null
        }
    }
}

enum class Profile {
    LOCAL, DEV, PROD
}

fun config() = when (System.getenv("NAIS_CLUSTER_NAME")
    ?: System.getProperty("NAIS_CLUSTER_NAME")) {
    "dev-sbs" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-sbs" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> {
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding localProperties
    }
}
