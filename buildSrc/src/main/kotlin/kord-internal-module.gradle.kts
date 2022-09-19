import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    org.jetbrains.kotlin.jvm
}

repositories {
    mavenCentral()
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = Jvm.targetString
        targetCompatibility = Jvm.targetString
    }

    withType<KotlinCompile> {
        kotlinOptions {
            applyKordKotlinOptions()
        }
    }
}
