import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `kotlin-dsl`
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("serialization"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.7.0")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.18.2")
    implementation(gradleApi())
    implementation(localGroovy())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.languageVersion = "1.5"
}
