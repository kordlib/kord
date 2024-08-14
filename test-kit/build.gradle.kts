plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.bundles.test.common)
                api(libs.ktor.utils)
            }
        }
        jsMain {
            dependencies {
                api(libs.bundles.test.js)
            }
        }
        jvmMain {
            dependencies {
                api(libs.bundles.test.jvm)
                runtimeOnly(libs.bundles.test.jvm.runtime)
            }
        }
    }
}
