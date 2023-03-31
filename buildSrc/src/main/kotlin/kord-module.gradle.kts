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
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvmToolchain(Jvm.target)

    sourceSets {
        // mark ksp src dir
        main { kotlin.srcDir("build/generated/ksp/main/kotlin") }

        // allow `ExperimentalCoroutinesApi` for `runTest {}`
        test { languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi") }
    }
}

configureAtomicFU()

tasks {
    withType<KotlinCompile> {
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
