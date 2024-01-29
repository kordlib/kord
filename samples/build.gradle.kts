import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

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
        if (konanTarget != KonanTarget.MINGW_X64) {
            binaries.executable {
                entryPoint = "dev.kord.voice.test.main"
            }
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
