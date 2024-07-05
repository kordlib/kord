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

                implementation(libs.kotlin.logging)

                // TODO remove when gatewayOnLogger and mu.KLogger.error() are removed
                implementation(libs.kotlin.logging.old)

                compileOnly(projects.kspAnnotations)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }
        jsMain {
            dependencies {
                implementation(libs.kotlin.node)
                implementation(npm("fast-zlib", libs.versions.fastZlib.get()))

                // workaround for https://youtrack.jetbrains.com/issue/KT-43500 /
                // https://youtrack.jetbrains.com/issue/KT-64109#focus=Comments-27-10064206.0-0
                // (intended to be compileOnly in commonMain only)
                implementation(projects.kspAnnotations)
            }
        }
    }
}
