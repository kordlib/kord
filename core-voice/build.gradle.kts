plugins {
    `kord-native-module`
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.voice)
            }
        }
    }
}
