import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

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
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
