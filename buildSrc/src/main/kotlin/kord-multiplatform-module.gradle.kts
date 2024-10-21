import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
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
    applyDefaultHierarchyTemplate {
        common {
            group("nonJvm") {
                withNative()
                withJs()
            }

            group("nonJs") {
                withNative()
                withJvm()
            }
        }
    }

    targets()
    explicitApi()
    compilerOptions {
        optIn.addAll(kordOptIns)
        applyKordCommonCompilerOptions()
    }

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

    withType<KotlinNativeTest>().configureEach {
        environment("PROJECT_ROOT", rootProject.projectDir.absolutePath)
    }

    withType<JavaCompile>().configureEach {
        options.release = KORD_JVM_TARGET
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

    withType<AbstractDokkaLeafTask>().configureEach {
        applyKordDokkaOptions()
        dependsOn("kspCommonMainKotlinMetadata")
    }

    disableLinuxLinkTestTasksOnWindows()
}
