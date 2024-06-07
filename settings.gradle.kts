plugins {
    // https://github.com/gradle/foojay-toolchains
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kord"

include(
    "bom",
    "common",
    "compiler-plugins",
    "core",
    "core:live-tests",
    "core-voice",
    "gateway",
    "ksp-annotations",
    "ksp-processors",
    "rest",
    "samples",
    "test-kit",
    "voice",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
