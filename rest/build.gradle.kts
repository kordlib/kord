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
                api(libs.bundles.stately)
                compileOnly(projects.kspAnnotations)

                // The plugin should add this automatically, but it doesn't
                compileOnly(libs.kotlinx.atomicfu)
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
