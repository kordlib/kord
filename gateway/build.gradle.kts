plugins {
    `kord-multiplatform-module`
    `kord-publishing`
    `kotlinx-serialization`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                compileOnly(projects.kspAnnotations)

                api(libs.bundles.ktor.client.serialization)
                api(libs.ktor.client.core)
                api(libs.kotlinx.coroutines.core)
                api(libs.ktor.client.websockets)

                // The plugin should add this automatically, but it doesn't
                compileOnly(libs.kotlinx.atomicfu)
            }
        }

        jvmMain {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }

        jsMain {
            dependencies {
                api(libs.ktor.client.js)
                api(libs.kotlinx.nodejs)
                api(npm("fast-zlib", "2.0.1"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.test.common)
                implementation(libs.ktor.client.mock)
            }
        }

        jsTest {
            dependencies {
                implementation(libs.bundles.test.js)
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
