plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }
}

dependencies {
    implementation(libs.bundles.pluginsForBuildSrc)
    constraints {
        implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0") {
            because(
                "Binary compatibility validator 0.15.0-Beta.3 uses org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.2 " +
                    "as an implementation dependency (see [1] and [2]), which doesn't support Kotlin metadata " +
                    "version 2.1.0. kotlinx-metadata-jvm was replaced by kotlin-metadata-jvm with Kotlin 2.0.0 (see " +
                    "[3]), but the new artifact is not binary compatible with the old one (the root package of its " +
                    "classes was changed). So we use the latest version of kotlinx-metadata-jvm (0.9.0) for now.\n\n" +
                    "[1] https://github.com/Kotlin/binary-compatibility-validator/blob/0.15.0-Beta.3/gradle/libs.versions.toml#L18\n" +
                    "[2] https://github.com/Kotlin/binary-compatibility-validator/blob/0.15.0-Beta.3/build.gradle.kts#L67\n" +
                    "[3] https://kotlinlang.org/docs/whatsnew20.html#the-kotlinx-metadata-jvm-library-is-stable"
            )
        }
    }
}
