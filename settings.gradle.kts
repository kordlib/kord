rootProject.name = "kord"

include(
    "bom",
    "common",
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
