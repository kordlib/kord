plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

kotlin {
    jvmToolchain(Jvm.target)
    compilerOptions {
        applyKordCompilerOptions()
    }
}
