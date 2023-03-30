plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    sourceSets {
        applyKordOptIns()
        addTestKit(targets)
        commonTest {
            dependencies {
                implementation(projects.core)
            }
        }
    }
}

tasks {
    withType<AbstractTestTask> {
        enabled = !System.getenv("KORD_TEST_TOKEN").isNullOrBlank()
    }
}
