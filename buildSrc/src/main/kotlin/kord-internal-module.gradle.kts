import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = Jvm.target
        targetCompatibility = Jvm.target
    }

    withType<KotlinCompile> {
        kotlinOptions {
            kordJvmOptions()
        }
    }
}
