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
    sourceSets {
        applyKordSourceSetOptions()
    }
}

tasks {
    withType<AbstractTestTask> {
        enabled = !System.getenv("KORD_TEST_TOKEN").isNullOrBlank()
    }
}
