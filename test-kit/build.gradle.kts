plugins {
    `kord-internal-multiplatform-module`
    `kord-native-module`
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
            }
        }
    }
}
