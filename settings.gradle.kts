@file:Suppress("UnstableApiUsage")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.dokka") {
                useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kord"
include("gateway")
include("common")
include("rest")
include("core")
include("voice")
include("bom")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            kotlinx()
            ktor()
            cache()
            common()
            tests()
        }
    }
}

fun VersionCatalogBuilder.cache() {
    val cache = version("kord-cache") {
        strictly("[0.3.0, 0.4.0[")
        prefer("latest.release")
    }

    alias("kord-cache-api").to("dev.kord.cache", "cache-api").versionRef(cache)
    alias("kord-cache-map").to("dev.kord.cache", "cache-map").versionRef(cache)
}

fun VersionCatalogBuilder.kotlinx() {
    alias("kotlinx-datetime").to("org.jetbrains.kotlinx", "kotlinx-datetime").version("0.3.1")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "1.6.7")

    alias("ktor-client-json").to("io.ktor", "ktor-client-json").versionRef(ktor)
    alias("ktor-client-serialization").to("io.ktor", "ktor-client-serialization").versionRef(ktor)

    alias("ktor-client-cio").to("io.ktor", "ktor-client-cio").versionRef(ktor)

    alias("ktor-client-websockets").to("io.ktor", "ktor-client-websockets").versionRef(ktor)


    alias("ktor-client-mock").to("io.ktor", "ktor-client-mock").versionRef(ktor)

    alias("ktor-network").to("io.ktor", "ktor-network").versionRef(ktor)
}

fun VersionCatalogBuilder.common() {
    version("kotlinx-coroutines", "1.6.0")
    alias("kotlinx-serialization").to("org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.3.2")
    alias("kotlinx-coroutines").to("org.jetbrains.kotlinx", "kotlinx-coroutines").versionRef("kotlinx-coroutines")
    alias("kotlinx-atomicfu").to("org.jetbrains.kotlinx", "atomicfu").version("0.17.0")
    alias("kotlin-logging").to("io.github.microutils", "kotlin-logging").version("2.1.21")

    bundle("common", listOf("kotlinx-serialization", "kotlinx-coroutines", "kotlinx-atomicfu", "kotlin-logging"))
}

fun VersionCatalogBuilder.tests() {
    val junit5 = version("junit5", "5.8.2")

    alias("mockk").to("io.mockk", "mockk").version("1.12.1")
    alias("kotlinx-coroutines").to("org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef("kotlinx-coroutines")
    alias("junit-jupiter-api").to("org.junit.jupiter", "junit-jupiter-api").versionRef(junit5)

    alias("slf4j-simple").to("org.slf4j", "slf4j-simple").version("1.7.30")
    alias("junit-jupiter-engine").to("org.junit.jupiter", "junit-jupiter-engine").versionRef(junit5)

    bundle("test-implementation", listOf("mockk", "kotlinx-coroutines", "junit-jupiter-api"))

    bundle(
        "test-runtime", listOf(
            "slf4j-simple",
            "junit-jupiter-engine"
        )
    )
}
