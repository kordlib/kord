plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(Jvm.target)
    compilerOptions {
        applyKordCompilerOptions()
    }
}
