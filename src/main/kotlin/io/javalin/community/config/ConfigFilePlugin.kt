package io.javalin.community.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.javalin.config.JavalinConfig
import io.javalin.plugin.Plugin
import io.javalin.plugin.PluginPriority
import io.javalin.plugin.PluginPriority.EARLY
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer

data class ConfigFilePluginConfig(
    var workingDirectory: Path = Paths.get("").toAbsolutePath(),
)

class ConfigFilePlugin(
    config: Consumer<ConfigFilePluginConfig>
) : Plugin<ConfigFilePluginConfig>(
    userConfig = config,
    defaultConfig = ConfigFilePluginConfig()
) {

    override fun onInitialize(config: JavalinConfig) {
        val defaultConfigFile = loadConfiguration(DefaultConfigFile::class.java)
        defaultConfigFile.applyConfiguration(config)
    }

    inline fun <reified C : Any> loadConfiguration(): C =
        loadConfiguration(C::class.java)

    fun <C : Any> loadConfiguration(type: Class<C>): C =
        ConfigLoaderBuilder.default()
            .addResourceSource("/application.yml", optional = false, allowEmpty = true)
            .addFileSource(pluginConfig.workingDirectory.resolve("application.yml").toFile(), optional = true, allowEmpty = true)
            .allowEmptySources()
            .build()
            .loadConfigOrThrow(type.kotlin, emptyList())

    override fun priority(): PluginPriority =
        EARLY

}