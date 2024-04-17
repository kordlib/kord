import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    org.jetbrains.kotlin.multiplatform
    dev.kord.`gradle-tools`
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
    targets()
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
                runtimeOnly(libs.bundles.test.jvm.runtime)
            }
        }
        nativeMain {
            dependencies {
                api(libs.kotlinx.io)
            }
        }
    }
}
