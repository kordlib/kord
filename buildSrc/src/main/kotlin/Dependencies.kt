object Versions {
    const val kotlin = "1.5.21"
    const val kotlinxSerialization = "1.2.1"
    const val ktor = "1.6.0"
    const val kotlinxCoroutines = "1.5.0"
    const val kotlinLogging = "2.0.6"
    const val dateTime = "0.2.1"
    const val atomicFu = "0.16.1"
    const val binaryCompatibilityValidator = "0.5.0"
    const val xsala20poly1305 = "0.10.1"

    //test deps
    const val kotlinTest = kotlin
    const val junit5 = "5.7.1"
    const val kotlinxCoroutinesTest = kotlinxCoroutines
    const val kotlinReflect = kotlin
    const val junitJupiterApi = junit5
    const val junitJupiterEngine = junit5
    const val sl4j = "1.7.30"
    const val bintray = "1.8.5"
    const val mockk = "1.10.6"
}

@Suppress("ObjectPropertyName")
object Dependencies {
    const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val `kotlinx-serialization` =
        "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
    const val `kotlinx-serialization-json` =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    const val `kotlinx-coroutines` = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}"
    const val `kotlinx-atomicfu` = "org.jetbrains.kotlinx:atomicfu-jvm:${Versions.atomicFu}"
    const val `kotlinx-datetime` = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.dateTime}"

    const val `kotlin-logging` = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"

    const val `kotlin-test` = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlinTest}"
    const val junit5 = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlinTest}"
    const val `kotlinx-coroutines-test` =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinxCoroutinesTest}"
    const val `kotlin-reflect` = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinReflect}"
    const val `junit-jupiter-api` = "org.junit.jupiter:junit-jupiter-api:${Versions.junitJupiterApi}"
    const val `junit-jupiter-engine` = "org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiterEngine}"
    const val sl4j = "org.slf4j:slf4j-simple:${Versions.sl4j}"

    const val `ktor-client-json` = "io.ktor:ktor-client-json:${Versions.ktor}"
    const val `ktor-client-json-jvm` = "io.ktor:ktor-client-json-jvm:${Versions.ktor}"
    const val `ktor-client-serialization-jvm` = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
    const val `ktor-client-cio` = "io.ktor:ktor-client-cio:${Versions.ktor}"
    const val `ktor-client-websocket` = "io.ktor:ktor-client-websockets:${Versions.ktor}"
    const val `ktor-client-mock` = "io.ktor:ktor-client-mock:${Versions.ktor}"
    const val `ktor-client-mock-jvm` = "io.ktor:ktor-client-mock-jvm:${Versions.ktor}"
    const val `ktor-network` = "io.ktor:ktor-network:${Versions.ktor}"

    const val `codahale-xsalsa20poly1305` = "com.codahale:xsalsa20poly1305:${Versions.xsala20poly1305}"

    const val `cache-api` = "dev.kord.cache:cache-api"
    const val `cache-map` = "dev.kord.cache:cache-map"

    const val mockk = "io.mockk:mockk:${Versions.mockk}"
}

object Plugins {
    const val kapt = "org.jetbrains.kotlin.kapt"
}