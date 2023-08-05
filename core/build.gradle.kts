import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11

plugins {
    `kord-native-module`
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

                implementation(libs.kotlin.logging)

                // TODO remove when kordLogger is removed
                implementation(libs.kotlin.logging.old)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
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
