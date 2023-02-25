@Suppress("DSL_SCOPE_VIOLATION") // false positive for `libs` in IntelliJ
plugins {
    `kord-multiplatform-module`
    `kotlinx-atomicfu`
    `kotlinx-serialization`
    alias(libs.plugins.buildconfig)
}

kotlin {
    explicitApi()

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
                implementation(libs.kotlin.test.js)
                implementation(libs.kotlinx.nodejs)
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
