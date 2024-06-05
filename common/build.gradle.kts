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
                api(libs.ktor.client.core)

                compileOnly(projects.kspAnnotations)
            }
        }
        jvmMain {
            dependencies {
                api(libs.ktor.client.okhttp)
            }
        }
        nonJvmMain {
            dependencies {
                implementation(libs.ktor.utils)
                implementation(libs.bignum)
                implementation(libs.stately.collections)
            }
        }
        jsMain {
            dependencies {
                api(libs.ktor.client.js)

                // workaround for https://youtrack.jetbrains.com/issue/KT-43500
                // (intended to be compileOnly in commonMain only)
                implementation(projects.kspAnnotations)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.bson)
                implementation(libs.kbson)
            }
        }
    }
}

/*
This will generate a file named "BuildConfigGenerated.kt" that looks like:

package dev.kord.common

internal const val BUILD_CONFIG_GENERATED_LIBRARY_VERSION: String = "<version>"
internal const val BUILD_CONFIG_GENERATED_COMMIT_HASH: String = "<commit hash>"
internal const val BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH: String = "<short commit hash>"
*/
buildConfig {
    packageName = "dev.kord.common"
    className = "BuildConfigGenerated"

    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField("BUILD_CONFIG_GENERATED_LIBRARY_VERSION", libraryVersion)
    buildConfigField("BUILD_CONFIG_GENERATED_COMMIT_HASH", commitHash)
    buildConfigField("BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH", shortCommitHash)
}
