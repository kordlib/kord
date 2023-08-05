import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    org.jetbrains.kotlin.jvm
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
    ksp(project(":ksp-processors"))
}

apiValidation {
    applyKordBCVOptions()
}

kotlin {
    explicitApi()

    jvmToolchain(Jvm.target)

    compilerOptions {
        applyKordCompilerOptions()
        optIn.addAll(kordOptIns)
    }

    sourceSets {
        // allow `ExperimentalCoroutinesApi` for `TestScope.currentTime`
        test { languageSettings.optIn(OptIns.coroutines) }
    }
}

configureAtomicFU()

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<AbstractDokkaLeafTask>().configureEach {
        applyKordDokkaOptions()
    }
}

mavenPublishing {
    configure(KotlinJvm(javadocJar = JavadocJar.Dokka("dokkaHtml")))
}
