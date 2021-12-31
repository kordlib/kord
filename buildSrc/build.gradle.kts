plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.6.10"))
    implementation(kotlin("serialization", version = "1.6.10"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.6.0")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.17.0")
    implementation(gradleApi())
    implementation(localGroovy())
}
