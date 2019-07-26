package no.nav.dagpenger.innsyn.parsing

import mu.KotlinLogging
import no.nav.dagpenger.events.inntekt.v1.SpesifisertInntekt
import no.nav.dagpenger.events.moshiInstance
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate

val logger = KotlinLogging.logger {}

fun getJSONParsed(userName: String): SpesifisertInntekt {
    logger.debug("Atttempting to find JSON file for $userName, this should only run during tests")
    return moshiInstance.adapter(SpesifisertInntekt::class.java)
            .fromJson(FileInputStream(Files.find(Paths.get(""), 10, BiPredicate { path, basicFileAttributes ->
                basicFileAttributes.isRegularFile &&
                        path.toAbsolutePath().toString()
                                .matches(Regex("^.+src.+%s.json$".format(userName)))
            })
                    .findFirst().get().toFile()).toString())!!
}