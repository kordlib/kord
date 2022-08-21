import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.11.0"
    id("org.jetbrains.dokka")

    signing
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.30.0"
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

tasks {
    wrapper {
        // Steps for upgrading Gradle:
        // 1. update `gradleVersion` and `distributionSha256Sum`
        //    (use 'Complete (-all) ZIP Checksum' found here: https://gradle.org/release-checksums/)
        // 2. run `./gradlew wrapper`
        //    (will update 'gradle/wrapper/gradle-wrapper.properties')
        // 3. run `./gradlew wrapper` again
        //    (might update 'gradle/wrapper/gradle-wrapper.jar', 'gradlew' and 'gradlew.bat')
        // 4. commit all changes

        gradleVersion = "7.5.1"
        distributionType = ALL
        distributionSha256Sum = "db9c8211ed63f61f60292c69e80d89196f9eb36665e369e7f00ac4cc841c2219"
    }

    val dokkaOutputDir = rootProject.projectDir.resolve("dokka")

    clean {
        delete(dokkaOutputDir)
    }

    dokkaHtmlMultiModule {
        dependsOn(clean)
        failOnWarning.set(true)
        outputDirectory.set(dokkaOutputDir)
    }
}
