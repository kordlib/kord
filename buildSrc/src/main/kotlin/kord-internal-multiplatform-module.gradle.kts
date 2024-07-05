import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
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
