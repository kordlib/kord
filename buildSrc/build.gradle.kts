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
    implementation(kotlin("gradle-plugin-api", version = "1.5.21"))
    implementation(gradleApi())
    implementation(localGroovy())
}
