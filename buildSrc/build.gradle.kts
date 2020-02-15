plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin-api", version = "1.3.61"))
    implementation(gradleApi())
    implementation(localGroovy())
}