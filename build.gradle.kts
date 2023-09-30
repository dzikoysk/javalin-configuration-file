import com.bnorm.power.PowerAssertGradleExtension

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.bnorm.power.kotlin-power-assert") version "0.13.0"
    `maven-publish`
    `java-library`
}

group = "io.javalin.community"
version = "6.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.reposilite.com/snapshots")
}

publishing {
    repositories {
        maven {
            name = "reposilite-repository"
            url = uri("https://maven.reposilite.com/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")

            credentials {
                username = getEnvOrProperty("MAVEN_NAME", "mavenUser")
                password = getEnvOrProperty("MAVEN_TOKEN", "mavenPassword")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components.getByName("java"))
        }
    }
}

dependencies {
    val javalinVersion = "6.0.0-SNAPSHOT"
    compileOnly("io.javalin:javalin:$javalinVersion")
    testImplementation("io.javalin:javalin:$javalinVersion")
    testImplementation("io.javalin:javalin-testtools:6.0.0-SNAPSHOT")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

configure<PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertTrue",
        "kotlin.test.assertFalse",
    )
}

fun getEnvOrProperty(env: String, property: String): String? =
    System.getenv(env) ?: findProperty(property)?.toString()