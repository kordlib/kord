import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    org.jetbrains.kotlinx.atomicfu
    com.google.devtools.ksp
}

repositories {
    mavenCentral()
}

dependencies {
    ksp(project(":ksp-processors"))
}

kotlin {
    explicitApi()
    compilerOptions {
        applyKordJvmCompilerOptions()
        optIn.addAll(kordOptIns)
    }

    sourceSets {
        applyKordTestOptIns()
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        applyKordBCVOptions()
    }
}

dokka {
    applyKordDokkaOptions(project)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<JavaCompile>().configureEach {
        options.release = KORD_JVM_TARGET
    }
}
