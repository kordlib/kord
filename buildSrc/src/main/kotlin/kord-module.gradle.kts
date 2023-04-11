import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    `kotlinx-atomicfu`
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
    `maven-publish`
    org.graalvm.buildtools.native
}

repositories {
    mavenCentral()
}

dependencies {
    ksp(project(":ksp-processors"))
}

apiValidation {
    applyKordBCVOptions()
}

kotlin {
    explicitApi()

    jvmToolchain(Jvm.target)

    sourceSets {
        // allow `ExperimentalCoroutinesApi` for `runTest {}`
        test { languageSettings.optIn(OptIns.coroutines) }
    }
}

ksp {
    arg("project", project.name)
}

configureAtomicFU()

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            applyKordCompilerOptions()
            freeCompilerArgs.addAll(kordOptIns.map { "-opt-in=$it" })
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<AbstractDokkaLeafTask>().configureEach {
        applyKordDokkaOptions()
    }

    withType<PublishToMavenRepository>().configureEach {
        doFirst { require(!Library.isUndefined) { "No release/snapshot version found." } }
    }
}

publishing {
    publications.register<MavenPublication>(Library.name) {
        from(components["java"])
        artifact(tasks.kotlinSourcesJar)
    }
}

graalvmNative {
    binaries.all {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.GRAAL_VM)
        })
    }

    binaries.named("test") {
        configurationFileDirectories.from(rootProject.file("graalvm-native-image-test-config"))
        resources {
            autodetection {
                enabled.set(true)
                restrictToProjectDependencies.set(false)
            }
            // language=regexp
            includedPatterns.addAll(""".*\.json""", """.*\.png""")
        }
    }
}
