plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "kord"

include(
    "bom",
    "common",
    "core",
    "gateway",
    "graal-example",
    "ksp-annotations",
    "ksp-processors",
    "rest",
    "voice",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
