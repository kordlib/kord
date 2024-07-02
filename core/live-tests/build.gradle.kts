import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    org.jetbrains.kotlin.multiplatform
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(kordOptIns)
    }
    targets()
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
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    disableLinuxLinkTestTasksOnWindows()
}
