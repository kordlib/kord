import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `kord-internal-multiplatform-module`
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
