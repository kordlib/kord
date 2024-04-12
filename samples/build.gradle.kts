import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    org.jetbrains.kotlin.multiplatform
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("voice") {
                withLinux()
                withMacos()
                withJvm()
                withJs()
            }
        }
    }

    targets()

    js {
        binaries.executable()
    }

    targets.withType<KotlinNativeTarget> {
        // Voice does not target windows, so we disable it
        if (konanTarget.family != Family.MINGW) {
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
