import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvm {
        withJava()
    }

    // For now let's only support desktop here
    macosArm64()
    macosX64()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                api(projects.gateway)

                compileOnly(projects.kspAnnotations)

                api(libs.ktor.network)
            }
        }

        named("nativeMain") {
            dependencies {
                implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.8.9")
            }
        }
    }
}
