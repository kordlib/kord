@Suppress("DSL_SCOPE_VIOLATION") // false positive for `libs` in IntelliJ
plugins {
    `kord-multiplatform-module`
    `kord-publishing`
    alias(libs.plugins.buildconfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)
                api(libs.kotlin.logging)
                // Replacement for java.io
                api(libs.ktor.utils)
                api(libs.ktor.client.core)

                compileOnly(projects.kspAnnotations)
            }
        }
        jvmMain {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }
        nonJvm {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.bundles.stately)
            }
        }
        jsMain {
            dependencies {
                api(libs.ktor.client.js)
                // When targeting K/JS needs to be present at compile time for users
                api(projects.kspAnnotations)
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.kspProcessors)
}

/*
This will generate a file named "BuildConfigGenerated.kt" that looks like:

package dev.kord.common

internal const val BUILD_CONFIG_GENERATED_LIBRARY_VERSION: String = "<version>"
internal const val BUILD_CONFIG_GENERATED_COMMIT_HASH: String = "<commit hash>"
internal const val BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH: String = "<short commit hash>"
*/
buildConfig {
    packageName("dev.kord.common")
    className("BuildConfigGenerated")

    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField("String", "BUILD_CONFIG_GENERATED_LIBRARY_VERSION", "\"${Library.version}\"")
    buildConfigField("String", "BUILD_CONFIG_GENERATED_COMMIT_HASH", "\"${Library.commitHash}\"")
    buildConfigField("String", "BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH", "\"${Library.shortCommitHash}\"")
}
