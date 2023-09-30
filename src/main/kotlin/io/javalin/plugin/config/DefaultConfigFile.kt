package io.javalin.plugin.config

import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import io.javalin.plugin.config.Compression.CompressionType.BROTLI
import io.javalin.plugin.config.Compression.CompressionType.GZIP
import io.javalin.plugin.config.Compression.CompressionType.NONE
import io.javalin.util.ConcurrencyUtil

private interface DefaultConfigApplicator {
    fun applyConfiguration(config: JavalinConfig)
}

data class DefaultConfigFile(
    val server: Server? = null
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        server?.applyConfiguration(config)
    }

}

data class Server(
    val hostname: String? = null,
    val port: Int? = null,
    val banner: Boolean? = null,

    val http: Http? = null,
    val router: Router? = null,
    val jetty: Jetty? = null,
    val staticFiles: StaticFiles? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        // TODO: hostname
        // TODO: port
        banner?.let { config.showJavalinBanner = it }

        http?.applyConfiguration(config)
        router?.applyConfiguration(config)
        jetty?.applyConfiguration(config)
        staticFiles?.applyConfiguration(config)
    }

}

data class Http(
    val generateEtags: Boolean? = null,
    val prefer405over404: Boolean? = null,
    val maxRequestSize: Long? = null,
    val defaultContentType: String? = null,
    val asyncTimeout: Long? = null,
    val compression: Compression? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        config.http.let { http ->
            generateEtags?.let { http.generateEtags = it }
            prefer405over404?.let { http.prefer405over404 = it }
            maxRequestSize?.let { http.maxRequestSize = it }
            defaultContentType?.let { http.defaultContentType = it }
            asyncTimeout?.let { http.asyncTimeout = it }
            compression?.applyConfiguration(config)
        }
    }

}

data class Jetty(
    val threadPool: JettyThreadPool? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        threadPool?.applyConfiguration(config)
    }

}

data class JettyThreadPool(
    val maxThreads: Int? = null,
    val minThreads: Int? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        config.jetty.threadPool = ConcurrencyUtil.jettyThreadPool(
            "JettyServerThreadPool",
            minThreads ?: 8,
            maxThreads ?: 250
        )
    }

}

data class Compression(
    val strategy: CompressionType? = null,
    val level: Int? = null,
) : DefaultConfigApplicator {

    enum class CompressionType {
        GZIP,
        BROTLI,
        NONE
    }

    override fun applyConfiguration(config: JavalinConfig) {
        config.compression.apply {
            strategy?.let {
                when (it) {
                    GZIP -> gzipOnly(level ?: 6)
                    BROTLI -> brotliOnly(level ?: 4)
                    NONE -> none()
                }
            }
        }
    }

}

data class Router(
    val contextPath: String? = null,
    val ignoreTrailingSlashes: Boolean? = null,
    val treatMultipleSlashesAsSingleSlash: Boolean? = null,
    val caseInsensitiveRoutes: Boolean? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        config.router.let { router ->
            contextPath?.let { router.contextPath = it }
            ignoreTrailingSlashes?.let { router.ignoreTrailingSlashes = it }
            treatMultipleSlashesAsSingleSlash?.let { router.treatMultipleSlashesAsSingleSlash = it }
            caseInsensitiveRoutes?.let { router.caseInsensitiveRoutes = it }
        }
    }

}

data class StaticFiles(
    val enableWebjars: Boolean? = null,
    val directories: List<StaticFileDirectory>? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        enableWebjars?.run { config.staticFiles.enableWebjars() }
        directories?.forEach { it.applyConfiguration(config) }
    }

}

data class StaticFileDirectory(
    val directory: String? = null,
    val location: Location? = null,
) : DefaultConfigApplicator {

    override fun applyConfiguration(config: JavalinConfig) {
        config.staticFiles.add { entry ->
            directory?.let { entry.directory = it }
            location?.let { entry.location = it }
        }

    }

}