plugins {
    `kord-targets`
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)

                api(libs.bundles.ktor.client.serialization)

                implementation(libs.kotlin.logging)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.ktor.client.mock)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }
    }
}
