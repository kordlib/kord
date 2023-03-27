plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                api(libs.ktor.resources)
                api(libs.bundles.ktor.client.serialization)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.ktor.client.mock)
            }
        }
    }
}
