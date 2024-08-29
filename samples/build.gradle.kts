plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    js {
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core)

                // these are required for whatever reason for Kotlin 2.1.0-dev-5441
                implementation(projects.common)
                implementation(projects.rest)
                implementation(projects.gateway)
            }
        }
        jvmMain {
            dependencies {
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
