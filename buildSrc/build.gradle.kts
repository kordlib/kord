plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "1.7.20"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.7.20")
    implementation("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", "0.18.5")
    implementation("com.google.devtools.ksp", "symbol-processing-gradle-plugin", "1.7.20-1.0.7")
    implementation(gradleApi())
}
