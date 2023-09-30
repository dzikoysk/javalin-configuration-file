# Javalin Config File Plugin ![Snapshot](https://maven.reposilite.com/api/badge/latest/snapshots/io/javalin/community/javalin-configuration-file?color=A97BFF&name=Snapshot)
Define static configuration for Javalin in a YAML file.

### Usage

Add the dependency to your project:

```kotlin
implementation("io.javalin.community:javalin-configuration-file:6.0.0-SNAPSHOT")
```

Register the plugin:

```kotlin
val application = Javalin.create {
    it.registerPlugin(ConfigFile) 
}
```

Order of precedence:
1. `application.yml` in working directory
2`application.yml` in resources

### Properties

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