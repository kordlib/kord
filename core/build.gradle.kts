plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10000" // KordEventDropTest is too slow for default 2 seconds timeout
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                api(projects.rest)
                api(projects.gateway)

                api(libs.kord.cache.api)
                api(libs.kord.cache.map)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}

tasks {
    dokkaHtmlMultiModule {
        enabled = false
    }
}
