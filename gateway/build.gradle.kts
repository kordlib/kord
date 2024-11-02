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

                implementation(libs.kotlin.logging)

                compileOnly(projects.kspAnnotations)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
                implementation(libs.zstd.jni)
            }
        }
        jsMain {
            dependencies {
                implementation(libs.kotlin.node)
                implementation(npm("fast-zlib", libs.versions.fastZlib.get()))
                implementation(npm("simple-zstd", "1.4.2"))

                // workaround for https://youtrack.jetbrains.com/issue/KT-43500 /
                // https://youtrack.jetbrains.com/issue/KT-64109#focus=Comments-27-10064206.0-0 /
                // https://youtrack.jetbrains.com/issue/KT-61096 (intended to be compileOnly in commonMain only)
                implementation(projects.kspAnnotations)
            }
        }
    }
}
