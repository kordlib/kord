@Suppress("DSL_SCOPE_VIOLATION") // false positive for `libs` in IntelliJ
plugins {
    `kord-multiplatform-module`
    `kord-publishing`
    `kotlinx-atomicfu`
    `kotlinx-serialization`
    `kord-sampled-multiplatform-module`
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
                implementation(libs.bundles.test.common)
                implementation(libs.ktor.client.mock)
            }
        }

        jsMain {
            dependencies {
                api(libs.ktor.client.js)
            }
        }

        jsTest {
            dependencies {
                implementation(libs.bundles.test.js)
            }
        }

        jvmMain {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }

        jvmTest {
            dependencies {
                runtimeOnly(libs.bundles.test.runtime)
                implementation(libs.kotlin.test.junit5)
            }
        }
    }
}
