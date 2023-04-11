plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "kord"

include(
    "bom",
    "common",
    "core",
    "core:live-tests",
    "core-voice",
    "gateway",
    "graalvm-native-image-example",
    "ksp-annotations",
    "ksp-processors",
    "rest",
    "samples",
    "test-kit",
    "voice",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
