plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    mavenLocal()
}

kotlin {
    jvmToolchain(Jvm.target)
    compilerOptions {
        applyKordCompilerOptions()
    }
}
