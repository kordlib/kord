plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
}

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
