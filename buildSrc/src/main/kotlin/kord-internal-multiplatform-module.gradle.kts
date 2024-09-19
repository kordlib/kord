plugins {
    org.jetbrains.kotlin.multiplatform
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    mavenLocal()
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
