plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    sourceSets {
        all {
            applyKordOptIns()
        }
        commonTest {
            dependencies {
                implementation(projects.core)
                implementation(projects.testKit)
            }
        }
    }
}

tasks {
    withType<AbstractTestTask>().configureEach {
        enabled = !System.getenv("KORD_TEST_TOKEN").isNullOrBlank()
    }
}
