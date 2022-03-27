plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`

    // see https://github.com/gmazzo/gradle-buildconfig-plugin
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.kotlinx.datetime)

    api(libs.bundles.common)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

/*
This will generate a file named "BuildConfigGenerated.kt" that looks like:

package dev.kord.common

internal const val BUILD_CONFIG_GENERATED_LIBRARY_VERSION: String = "<version>"
*/
buildConfig {
    packageName("dev.kord.common")
    className("BuildConfigGenerated")

    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField(
        type = "String",
        name = "BUILD_CONFIG_GENERATED_LIBRARY_VERSION",
        value = "\"${Library.version}\"",
    )
}
