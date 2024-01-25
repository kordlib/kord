plugins {
    `kord-native-module`
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    jvm {
       withJava()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                api(projects.gateway)

                api(libs.ktor.network)
                implementation(libs.kotlin.logging)

                compileOnly(projects.kspAnnotations)
            }
        }

        nonJvmMain {
            dependencies {
                implementation(libs.libsodium)
            }
        }
    }
}
