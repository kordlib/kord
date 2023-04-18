plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.pluginsForBuildSrc)
    implementation(libs.dokka.versioning.plugin)
}
