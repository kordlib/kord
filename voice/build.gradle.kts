plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    jvm {
       withJava()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.common)
            api(projects.gateway)

            implementation(libs.kotlin.logging)

            compileOnly(projects.kspAnnotations)
        }

        nonJsMain.dependencies {
            api(libs.ktor.network)
        }

        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-node:20.11.30-pre.723")
        }

        nonJvmMain.dependencies {
            implementation(libs.libsodium)
        }

        jvmMain.dependencies {
            implementation(libs.slf4j.api)
        }
    }
}
