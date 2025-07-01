import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    org.jetbrains.kotlinx.atomicfu
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
}

repositories {
    mavenCentral()
}

dependencies {
    kspCommonMainMetadata(project(":ksp-processors"))
}

apiValidation {
    applyKordBCVOptions()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()
    compilerOptions {
        applyKordCommonCompilerOptions()
        optIn.addAll(kordOptIns)
    }

    jvm {
        compilerOptions {
            applyKordJvmCompilerOptions()
        }
    }
    js {
        nodejs {
            testTask {
                useMocha {
                    // disable timeouts, some tests are too slow for default 2-second timeout:
                    // https://mochajs.org/#-timeout-ms-t-ms
                    timeout = "0"
                }
            }
        }
        useCommonJs()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        applyKordTestOptIns()
        commonMain {
            // mark ksp src dir
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonTest {
            dependencies {
                implementation(project(":test-kit"))
            }
        }
        val nonJvmMain by creating {
            dependsOn(commonMain.get())
        }
        jsMain {
            dependsOn(nonJvmMain)
        }
    }
}

dokka {
    applyKordDokkaOptions(project)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<KotlinJsTest>().configureEach {
        environment("PROJECT_ROOT", rootProject.projectDir.absolutePath)
    }

    for (task in listOf(
        "compileKotlinJvm",
        "compileKotlinJs",
        "jvmSourcesJar",
        "jsSourcesJar",
        "dokkaGenerateModuleHtml",
        "dokkaGeneratePublicationHtml",
    )) {
        named(task) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }

    afterEvaluate {
        named("sourcesJar") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}
