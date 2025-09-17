@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    org.jetbrains.kotlinx.atomicfu
    com.google.devtools.ksp
}

repositories {
    mavenCentral()
}

dependencies {
    kspCommonMainMetadata(project(":ksp-processors"))
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()
    compilerOptions {
        applyKordCommonCompilerOptions()
        optIn.addAll(kordOptIns)
    }

    targets()

    applyDefaultHierarchyTemplate {
        common {
            group("nonJvm") {
                withJs()
                withWasmJs()
                withWasmWasi()
                withNative()
            }
        }
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

    abiValidation {
        applyKordBCVOptions()
        klib {
            enabled = true
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

    val compileTasks = (kotlin.targets.names - "metadata").flatMap {
        listOf("compileKotlin${it.replaceFirstChar { char -> char.uppercase() }}", "${it}SourcesJar")
    }

    for (task in compileTasks + listOf("dokkaGenerateModuleHtml", "dokkaGeneratePublicationHtml")) {
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
