import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

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

            group("nonNative") {
                withJs()
                withJvm()
            }
        }
    }

    explicitApi()

    jvm()

    if (name != "voice" && name != "core-voice") {
        js {
            nodejs()
        }
    }
    jvmToolchain(Jvm.target)

    targets.all {
        compilations.all {
            compilerOptions.options.applyKordCompilerOptions()
        }
    }

    sourceSets {
        all {
            applyKordOptIns()
        }
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

configureAtomicFU()

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

    afterEvaluate {
        val compilationTasks = kotlin.targets.flatMap {
            buildList {
                add("compileKotlin${it.name.capitalized()}")
                val sourcesJarName = "${it.name}SourcesJar"
                add(sourcesJarName)
            }
        }
        for (task in compilationTasks) {
            named(task) {
                dependsOn("kspCommonMainKotlinMetadata")
            }
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
}
