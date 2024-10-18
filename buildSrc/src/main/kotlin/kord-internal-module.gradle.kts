plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

kotlin {
    compilerOptions {
        applyKordJvmCompilerOptions()
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = KORD_JVM_TARGET
    }
}
