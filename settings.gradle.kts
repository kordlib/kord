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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

    library("kord-cache-api", "dev.kord.cache", "cache-api").versionRef(cache)
    library("kord-cache-map", "dev.kord.cache", "cache-map").versionRef(cache)
}

fun VersionCatalogBuilder.kotlinx() {
    library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.3.3")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "2.0.2")

    library("ktor-client-json","io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)
    library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(ktor)

    library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef(ktor)

    library("ktor-client-websockets", "io.ktor", "ktor-client-websockets").versionRef(ktor)


    library("ktor-client-mock", "io.ktor", "ktor-client-mock").versionRef(ktor)

    library("ktor-network", "io.ktor", "ktor-network").versionRef(ktor)

    bundle("ktor-client-serialization", listOf("ktor-client-content-negotiation", "ktor-client-json"))
}

fun VersionCatalogBuilder.common() {
    version("kotlinx-coroutines", "1.6.2")
    library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.3.3")
    library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinx-coroutines")
    library("kotlinx-atomicfu", "org.jetbrains.kotlinx", "atomicfu").version("0.17.3")
    library("kotlin-logging", "io.github.microutils", "kotlin-logging").version("2.1.23")

    bundle("common", listOf("kotlinx-serialization", "kotlinx-coroutines", "kotlinx-atomicfu", "kotlin-logging"))
}

fun VersionCatalogBuilder.tests() {
    val junit5 = version("junit5", "5.8.2")

    library("mockk", "io.mockk", "mockk").version("1.12.4")
    library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef("kotlinx-coroutines")
    library("junit-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(junit5)

    library("slf4j-simple", "org.slf4j", "slf4j-simple").version("1.7.36")
    library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(junit5)

    bundle("test-implementation", listOf("mockk", "kotlinx-coroutines-test", "junit-jupiter-api"))

    bundle(
        "test-runtime", listOf(
            "slf4j-simple",
            "junit-jupiter-engine"
        )
    )
}
