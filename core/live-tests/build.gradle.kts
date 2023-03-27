import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core)
                implementation(projects.kspAnnotations)
            }
        }
        addTestKit(targets)
    }
    targets {
        all {
            compilations.all {
                compilerOptions.options.applyKordCompilerOptions()
            }
        }
    }
}

tasks {
    withType<AbstractTestTask> {
        enabled = !System.getenv("KORD_TEST_TOKEN").isNullOrBlank()
    }

    // Replace this once proper project isolation support is there
    task<DokkaTaskPartial>("dokkaHtmlPartial") {
        enabled = false
    }
}
