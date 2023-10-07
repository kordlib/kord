plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    jvm()
    macosArm64()
    macosX64()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.voice)
            }
        }
    }
}
