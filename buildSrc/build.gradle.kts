plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "1.7.10"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.7.10")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.18.2")
    implementation(gradleApi())
}
