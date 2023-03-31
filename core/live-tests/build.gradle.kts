plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    addTestKit()

    sourceSets {
        all {
            applyKordOptIns()
        }

        commonTest {
            dependencies {
                implementation(projects.core)
            }
        }
    }
}

tasks {
    withType<AbstractTestTask>().configureEach {
        enabled = !System.getenv("KORD_TEST_TOKEN").isNullOrBlank()
    }
}
