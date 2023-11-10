# Javalin Config File Plugin ![Snapshot](https://maven.reposilite.com/api/badge/latest/snapshots/io/javalin/community/javalin-configuration-file?color=A97BFF&name=Snapshot)
Define static configuration file for your Javalin app in a YAML file.
You don't have to use 3rd party libraries to handle configuration files anymore.

### Usage

Add the dependency to your project:

```kotlin
implementation("io.javalin.community:javalin-configuration-file:6.0.0-SNAPSHOT")
```

Register the plugin:

```kotlin
val runningApplication = Javalin.createAndStart {
    it.registerPlugin(ConfigFile) 
}
```

Order of precedence:
1. `application.yml` in working directory
2. `application.yml` in resources

### Default properties

All properties are optional, so you can define only the ones you need.

```yaml
server:
  port: 8080
  hostname: localhost
  banner: false
  http:
    default-content-type: application/panda
    prefer-405-over-404: true
    max-request-size: 100000
    async-timeout: 30000
    compression:
      strategy: GZIP
      level: 5
  jetty:
    min-threads: 10
    max-threads: 200
  router:
    content-path: /panda
    ignore-trailing-slashes: true
    treat-multiple-slashes-as-single-slash: true
    case-insensitive-routes: true
  static-files:
    enable-webjars: true
    directories:
      - directory: /public
        location: CLASSPATH
```

### Custom properties

You can define custom properties for you application or other plugins by simply adding them to the YAML file.

```yaml
my-plugin:
  property: value
```

Then, create a data class to hold the values:

```kotlin
// Root scope for a config file
data class ConfigFile(
    // Your configs
    val myPlugin: MyPluginConfig
)

data class MyPluginConfig(val property: String)
```

Finally, you can load defined values via `ConfigFile` plugin API:

```kotlin
val application = Javalin.createAndStart {
    val configPlugin = it.registerPlugin(ConfigFile)
    val myPluginConfig = configPlugin.loadConfiguration<MyPluginConfig>()
}
```

Done!