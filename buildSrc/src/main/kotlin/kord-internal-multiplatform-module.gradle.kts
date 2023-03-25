plugins {
    org.jetbrains.kotlin.multiplatform
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

    sourceSets {
        applyKordSourceSetOptions()
    }
    targets {
        all {
            compilations.all {
                compilerOptions.options.applyKordCompilerOptions()
            }
        }
    }
}
