plugins {
    org.jetbrains.kotlin.jvm
    dev.kord.`gradle-tools`
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
