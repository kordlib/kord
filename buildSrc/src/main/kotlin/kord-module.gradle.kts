import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    org.jetbrains.kotlinx.atomicfu
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    mavenLocal()
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
        applyKordTestOptIns()
    }
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<AbstractDokkaLeafTask>().configureEach {
        applyKordDokkaOptions()
    }
}

publishing {
    publications.register<MavenPublication>(Library.name) {
        from(components["java"])
        artifact(tasks.kotlinSourcesJar)
    }
}
