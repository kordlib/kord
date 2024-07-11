import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        nodejs()
        useCommonJs()
    }
    jvmToolchain(Jvm.target)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        applyKordCompilerOptions()
    }
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
