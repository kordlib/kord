import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("ktor") {
                withJvm()
                withApple()
                withLinux()
            }

            group("nonKtor") {
                withJs()
                withMingw()
            }
        }
    }
    jvm {
        withJava()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common)
            api(projects.gateway)

            implementation(libs.kotlin.logging)

            compileOnly(projects.kspAnnotations)
        }

        named("ktorMain").dependencies {
            api(libs.ktor.network)
        }

        jsMain.dependencies {
            implementation(libs.kotlin.node)
        }

        nonJvmMain.dependencies {
            implementation(libs.libsodium)
        }

        jvmMain.dependencies {
            implementation(libs.slf4j.api)
        }
    }
}
