import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

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

kotlin {
    targetHierarchy.default {
        common {
            group("nonJvm") {
                withNative()
                withJs()
            }
        }
    }

    explicitApi()

    jvm()
    js {
        nodejs()
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
        val nonJvmMain by creating {
            dependsOn(commonMain.get())
        }
        jsMain {
            dependsOn(nonJvmMain)
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

    val compilationTasks = kotlin.targets.flatMap {
        buildList {
            add("compileKotlin${it.name.capitalized()}")
            if (it !is KotlinNativeTarget) {
                add("${it.name}SourcesJar")
            }
        }
    }
    for (task in compilationTasks) {
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
}
