plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.bundles.test.common)
                api(libs.ktor.utils)
                api(projects.common)
            }
        }
        jsMain {
            dependencies {
                api(libs.bundles.test.js)
            }
        }
        jvmMain {
            dependencies {
                api(libs.bundles.test.runtime)
                api(libs.kotlin.test.junit5)
            }
        }
    }
}
