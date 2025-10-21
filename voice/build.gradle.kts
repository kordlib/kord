plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.common)
            api(projects.gateway)
            api(libs.ktor.network)

            implementation(libs.kotlin.logging)

            compileOnly(projects.kspAnnotations)
        }

        nonJvmMain.dependencies {
            implementation(libs.libsodium)
        }

        jvmMain.dependencies {
            implementation(libs.slf4j.api)
        }
    }
}
