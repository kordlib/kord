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
                implementation(libs.kotlinx.nodejs)
                implementation(npm("fast-zlib", libs.versions.fastZlib.get()))
            }
        }
    }
}
