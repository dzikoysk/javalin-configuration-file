package io.javalin.community.config

import io.javalin.Javalin
import io.javalin.community.config.ConfigFilePlugin.Companion.ConfigFile
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import org.junit.jupiter.api.Test

internal class ConfigFilePluginTest {

    @Test
    fun `should load config file from classpath`() {
        val application = Javalin.create { it.registerPlugin(ConfigFile) }
        assert(application.unsafeConfig().http.defaultContentType == "application/panda")
    }

    @Test
    fun `should load config file from working directory`() {
        val workingDirectory = Files.createTempDirectory("javalin-config-file-plugin-test")
        Files.write(
            workingDirectory.resolve("application.yml"),
            """
            server:
              http:
                defaultContentType: "application/red-panda"
            """.trimIndent().toByteArray(),
            StandardOpenOption.CREATE,
        )

        val application = Javalin.create { cfg ->
            cfg.registerPlugin(ConfigFile) {
                it.workingDirectory = workingDirectory
            }
        }

        assert(application.unsafeConfig().http.defaultContentType == "application/red-panda")
    }


}