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
    //https://github.com/melix/japicmp-gradle-plugin/issues/36
    implementation("com.google.guava:guava:28.2-jre")

    implementation("me.champeau.gradle:japicmp-gradle-plugin:0.2.9")
    implementation(kotlin("gradle-plugin-api", version = "1.4.0"))
    implementation(gradleApi())
    implementation(localGroovy())
}