import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    js {
        nodejs()
        useCommonJs()
    }
    jvmToolchain(Jvm.target)

    targets.all {
        compilations.all {
            compilerOptions.options.applyKordCompilerOptions()
        }
    }
}
