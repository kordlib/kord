plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)

                api(libs.bundles.ktor.client.serialization)
                api(libs.ktor.client.core)
                api(libs.kotlinx.coroutines.core)
                api(libs.ktor.client.websockets)
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

dependencies {
    kspCommonMainMetadata(projects.kspProcessors)
}
