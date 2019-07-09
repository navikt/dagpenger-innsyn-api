package restapi

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.dagpenger.streams.KafkaCredential

private val localProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/dev/",
        "kafka.bootstrap.servers" to "localhost:9092",
        "application.profile" to "LOCAL",
        "application.httpPort" to "8092",
        "auth.secret" to "secret",
        "auth.allowedKeys" to "secret1, secret2",
        "srvdp.inntekt.innsyn.username" to "username",
        "srvdp.inntekt.innsyn.password" to "password"
    )
)
private val devProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/preprod-fss/",
        "kafka.bootstrap.servers" to "d26apvl00159.test.local:8443,d26apvl00160.test.local:8443,d26apvl00161.test.local:8443",
        "application.profile" to "DEV",
        "application.httpPort" to "8092"

    )
)
private val prodProperties = ConfigurationMap(
    mapOf(
        "vault.mountpath" to "postgresql/prod-fss/",
        "kafka.bootstrap.servers" to "a01apvl00145.adeo.no:8443,a01apvl00146.adeo.no:8443,a01apvl00147.adeo.no:8443,a01apvl00148.adeo.no:8443,a01apvl00149.adeo.no:8443,a01apvl150.adeo.no:8443",
        "application.profile" to "PROD",
        "application.httpPort" to "8092"
    )
)

private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
    "dev-fss" -> systemProperties() overriding EnvironmentVariables overriding devProperties
    "prod-fss" -> systemProperties() overriding EnvironmentVariables overriding prodProperties
    else -> {
        systemProperties() overriding EnvironmentVariables overriding localProperties
    }
}

internal data class Configuration(
    val vault: Vault = Vault(),
    val kafka: Kafka = Kafka(),
    val application: Application = Application()

) {
    data class Vault(
        val mountPath: String = config()[Key("vault.mountpath", stringType)]
    )

    data class Kafka(
        val brokers: String = config()[Key("kafka.bootstrap.servers", stringType)],
        val user: String? = config().getOrNull(Key("srvdp.innsyn.inntekt.username", stringType)),
        val password: String? = config().getOrNull(Key("srvdp.innsyn.inntekt.password", stringType))
    ) {
        fun credential(): KafkaCredential? {
            return if (user != null && password != null) {
                KafkaCredential(user, password)
            } else null
        }
    }

    data class Application(
        val profile: Profile = config()[Key("application.profile", stringType)].let { Profile.valueOf(it) },
        val httpPort: Int = config()[Key("application.httpPort", intType)]
    )
}

enum class Profile {
    LOCAL, DEV, PROD
}