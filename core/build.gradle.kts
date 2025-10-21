@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    `kord-multiplatform-module`
    `kord-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.common)
                api(projects.rest)
                api(projects.gateway)

                api(libs.kord.cache.api)
                api(libs.kord.cache.map)

                implementation(libs.kotlin.logging)
            }
        }
        nonJvmMain {
            dependencies {
                implementation(libs.stately.collections)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }

}
