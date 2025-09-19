import gradle.kotlin.dsl.accessors._6cf8ffb9e5fa3d9dd0ed334a95f1dc9c.java

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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = KORD_JVM_TARGET
    }
}
