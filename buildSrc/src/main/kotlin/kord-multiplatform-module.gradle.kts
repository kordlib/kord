import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
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


    register("publishNonNative") {
        dependsOn(
            "publishKotlinMultiplatformPublicationToMavenRepository",
            "publishJsPublicationToMavenRepository",
            "publishJvmPublicationToMavenRepository"
        )
    }

    register("publishLinux") {
        dependsOn("publishLinuxX64PublicationToMavenRepository")
    }

    register("publishMingw") {
        dependsOn("publishMingwX64PublicationToMavenRepository")
    }

    register("publishDarwin") {
        dependsOn(
            "publishMacosArm64PublicationToMavenRepository",
            "publishMacosX64PublicationToMavenRepository",
            "publishIosArm64PublicationToMavenRepository",
            "publishIosX64PublicationToMavenRepository",
            "publishWatchosX64PublicationToMavenRepository",
            "publishWatchosArm64PublicationToMavenRepository",
            "publishTvosX64PublicationToMavenRepository",
            "publishTvosArm64PublicationToMavenRepository"
        )
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
