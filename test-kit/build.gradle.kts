import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `kord-targets`
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("simulator") {
                withIos()
                withTvos()
                withWatchos()
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.bundles.test.common)
                api(libs.ktor.utils)
            }
        }
        jsMain {
            dependencies {
                api(libs.bundles.test.js)
            }
        }
        jvmMain {
            dependencies {
                api(libs.bundles.test.jvm)
            }
        }
        nativeMain {
            dependencies {
                api(libs.kotlinx.io)
            }
        }
    }
}
