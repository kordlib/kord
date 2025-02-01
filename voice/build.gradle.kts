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
                withMingw()
            }

            group("nonKtor") {
                withJs()
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
            api(libs.ktor.network)

            implementation(libs.kotlin.logging)

            compileOnly(projects.kspAnnotations)
        }

        nonJvmMain.dependencies {
            implementation(libs.libsodium)
        }

        jvmMain.dependencies {
            implementation(libs.slf4j.api)
        }
    }
}
