pluginManagement {
    resolutionStrategy {
        repositories {
            mavenLocal()
            maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
            mavenCentral()
            jcenter()
            gradlePluginPortal()
        }

        eachPlugin {
            val module = when (requested.id.id) {
                "org.jetbrains.kotlinx.atomicfu-gradle-plugin" -> "org.jetbrains.kotlinx:atomicfu-gradle-plugin"
                "org.jetbrains.kotlinx.binary-compatibility-validator" -> "org.jetbrains.kotlinx:binary-compatibility-validator"
                else -> null
            } ?: return@eachPlugin
            useModule("$module:${requested.version}")
        }
    }
}

rootProject.name = "kord"
include("gateway", "common", "rest", "core")
