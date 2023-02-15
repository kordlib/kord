import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(Jvm.target)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        applyKordCompilerOptions()
    }
}
