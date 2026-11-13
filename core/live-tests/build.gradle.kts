plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    compilerOptions {
        optIn.addAll(kordOptIns)
    }
    sourceSets {
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
