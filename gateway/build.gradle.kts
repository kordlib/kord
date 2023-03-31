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
                api(libs.ktor.client.websockets)
            }
        }
        jsMain {
            dependencies {
                api(libs.kotlinx.nodejs)
                api(npm("fast-zlib", "2.0.1"))
            }
        }
    }
}
