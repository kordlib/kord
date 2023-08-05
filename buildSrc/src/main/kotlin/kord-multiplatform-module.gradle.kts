import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    `kotlinx-atomicfu`
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
    id("com.vanniktech.maven.publish.base")
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
    explicitApi()

    jvm()
    js(IR) {
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
        targets
            .map { it.name }
            .filter { it != "jvm" && it != "metadata" }
            .forEach { target ->
                sourceSets.getByName("${target}Main") {
                    dependsOn(nonJvmMain)
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

    for (task in listOf("compileKotlinJvm", "compileKotlinJs", "jvmSourcesJar", "jsSourcesJar")) {
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

mavenPublishing {
    configure(KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaHtml")))
}
