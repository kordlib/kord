plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
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
