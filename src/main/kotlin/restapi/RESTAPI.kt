package restapi

import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.databind.SerializationFeature
import data.json.PersonRequest
import data.json.TotalInntekt
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import parsing.YearMonthDouble
import parsing.klaxonConverter
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        post("/inntekt") {
            val post = call.receive<PersonRequest>()
            getJSONparsed()?.let { it1 -> call.respond(it1) }
        }
    }
}

fun getJSONparsed(): TotalInntekt? {
    return Klaxon()
            .fieldConverter(YearMonthDouble::class, klaxonConverter)
            .parse<TotalInntekt>(InputStreamReader(Files
                    .newInputStream(Paths
                            .get(("src/test/resources/ExpectedJSONResultForUserPeter"
                                    .replace("/", File.separator))))))
}

