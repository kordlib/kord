import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    `kord-internal-multiplatform-module`
    `kord-native-module`
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("voice") {
                withLinux()
                withMacos()
                withJvm()
            }
        }
    }
    js {
        binaries.executable()
    }

    targets.withType<KotlinNativeTarget> {
        binaries.executable {
            entryPoint = "dev.kord.voice.test.main"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core)
                implementation(libs.kotlin.logging)
            }
        }
        jvmMain {
            dependencies {
                runtimeOnly(libs.slf4j.simple)
            }
        }
        linuxMain {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        named("voiceMain") {
            dependencies {
                implementation(projects.coreVoice)
            }
        }
    }
}
