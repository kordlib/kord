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

    targets.all {
        compilations.all {
            compilerOptions.options.applyKordCompilerOptions()
        }
    }
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
