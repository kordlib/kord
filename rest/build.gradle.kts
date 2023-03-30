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

dependencies {
    kspCommonMainMetadata(projects.kspProcessors)
}
