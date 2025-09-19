plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    // Repo providing the Kord Gradle plugin
    maven("https://europe-west3-maven.pkg.dev/mik-music/kord")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true

        optIn.add("org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl")
    }
}

dependencies {
    implementation(libs.bundles.pluginsForBuildSrc)
}
