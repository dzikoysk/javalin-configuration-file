package io.javalin.community.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.javalin.community.config.ConfigFilePlugin.ConfigFile
import io.javalin.config.JavalinConfig
import io.javalin.plugin.JavalinPlugin
import io.javalin.plugin.PluginConfiguration
import io.javalin.plugin.PluginFactory
import io.javalin.plugin.PluginPriority
import io.javalin.plugin.PluginPriority.EARLY
import io.javalin.plugin.createUserConfig
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer

data class ConfigFilePluginConfig(
    var workingDirectory: Path = Paths.get("").toAbsolutePath(),
) : PluginConfiguration

class ConfigFilePlugin(config: Consumer<ConfigFilePluginConfig>) : JavalinPlugin {

    open class ConfigFile : PluginFactory<ConfigFilePlugin, ConfigFilePluginConfig> {
        override fun create(config: Consumer<ConfigFilePluginConfig>): ConfigFilePlugin = ConfigFilePlugin(config)
    }

    companion object {
        object ConfigFile : ConfigFilePlugin.ConfigFile()
    }

    private val pluginConfig = config.createUserConfig(ConfigFilePluginConfig())

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

    override fun onStart(config: JavalinConfig) {
    }

    override fun priority(): PluginPriority =
        EARLY

}