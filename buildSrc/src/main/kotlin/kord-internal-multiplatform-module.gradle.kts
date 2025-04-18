@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    compilerOptions {
        applyKordCommonCompilerOptions()
    }

    jvm {
        compilerOptions {
            applyKordJvmCompilerOptions()
        }
    }
    js {
        nodejs()
        useCommonJs()
    }
    wasmJs {
        nodejs()
        useCommonJs()
    }
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
