pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.dokka") {
                useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
            }
        }
    }
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
        gradlePluginPortal()
    }
}

rootProject.name = "kord"
include("gateway")
include("common")
include("rest")
include("core")
include("voice")
