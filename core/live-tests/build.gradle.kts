plugins {
    org.jetbrains.kotlin.multiplatform
}

kotlin {
    targets()
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
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    disableLinuxLinkTestTasksOnWindows()
}
