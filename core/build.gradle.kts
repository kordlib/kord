plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                compileOnly(projects.kspAnnotations)
                api(projects.common)
                api(projects.rest)
                api(projects.gateway)

                api(libs.kord.cache.api)
                api(libs.kord.cache.map)
            }
        }

        commonTest {
            dependencies {
                // The plugin should add this automatically, but it doesn't
                compileOnly(libs.kotlinx.atomicfu)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}
