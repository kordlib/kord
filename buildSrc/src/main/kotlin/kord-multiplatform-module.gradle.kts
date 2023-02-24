import gradle.kotlin.dsl.accessors._e5121a5856746b077c6819bbe5a86a2f.main

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    `kotlinx-atomicfu`
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
    }
    jvmToolchain(Jvm.target)

    targets {
        all {
            compilations.all {
                compilerOptions.options.applyKordCompilerOptions()
            }
        }
    }

    sourceSets {
        all {
            if ("Test" in name) {
                languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
        commonMain {
            // mark ksp src dir
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}
