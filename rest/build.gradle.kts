@Suppress("DSL_SCOPE_VIOLATION") // false positive for `libs` in IntelliJ
plugins {
    `kord-multiplatform-module`
    `kord-publishing`
    `kotlinx-atomicfu`
    `kotlinx-serialization`
    alias(libs.plugins.buildconfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)

                api(libs.bundles.ktor.client.serialization)
                api(libs.ktor.client.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.testKit)
                implementation(libs.ktor.client.mock)
            }
        }

        jsMain {
            dependencies {
                api(libs.ktor.client.js)
            }
        }

        jvmMain {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }
    }
}
