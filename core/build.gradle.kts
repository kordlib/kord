import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    js {
        nodejs {
            testTask(Action {
                useMocha {
                    timeout = "10000" // KordEventDropTest is too slow for default 2 seconds timeout
                }
            })
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
    compileTestKotlinJvm {
        compilerOptions {
            // needed to inline MockK functions
            jvmTarget = JVM_11
        }
    }
    jvmTest {
        // needed to run the output of compileTestKotlinJvm targeting jvm 11
        javaLauncher = project.javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(11) }
    }
}
