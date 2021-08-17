plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("serialization"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.5.0")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.16.1")
    implementation(gradleApi())
    implementation(localGroovy())
}
