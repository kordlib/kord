plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // We currently cannot use Kotlin 1.5.31: https://github.com/kordlib/kord/issues/399
    implementation(kotlin("gradle-plugin", version = "1.5.30"))
    implementation(kotlin("serialization", version = "1.5.30"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.5.0")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.16.1")
    implementation(gradleApi())
    implementation(localGroovy())
}
