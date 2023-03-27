import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    `kotlinx-atomicfu`
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm()
    js(IR) {
        nodejs()
    }
    jvmToolchain(Jvm.target)

    targets {
        all {
            compilations.all {
                compilerOptions.options.applyKordCompilerOptions()
            }
        }
    }

    sourceSets {
        applyKordOptIns()
        commonMain {
            // mark ksp src dir
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

        val nonJvm by creating {
            dependsOn(commonMain.get())
        }

        addTestKit(targets)
        targets.forEach {
            if (it.safeName != "jvm" && it.safeName != "common") {
                findByName("${it.safeName}Main")?.apply {
                    dependsOn(nonJvm)
                }
            }
        }
    }
}

configureAtomicFU()

tasks {
    getByName<KotlinJvmTest>("jvmTest") {
        useJUnitPlatform()
    }

    withType<KotlinJsTest>() {
        environment("PROJECT_ROOT", rootProject.projectDir.absolutePath)
    }

    afterEvaluate {
        listOf("compileKotlinJvm", "compileKotlinJs", "jvmSourcesJar", "jsSourcesJar", "sourcesJar").forEach {
            getByName(it) {
                dependsOnKspKotlin("kspCommonMainKotlinMetadata")
            }
        }
    }

    afterEvaluate {
        configureDokka {
            dependsOnKspKotlin("kspCommonMainKotlinMetadata")
        }
    }
}