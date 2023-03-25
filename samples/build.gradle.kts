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
                compileOnly(projects.kspAnnotations)
            }
        }

        jvmMain {
            dependencies {
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
