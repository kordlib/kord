plugins {
    org.jetbrains.kotlin.jvm
    dev.kord.`gradle-tools`
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
