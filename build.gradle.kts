import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.10.1"
    id("org.jetbrains.dokka")

    id("org.ajoberstar.git-publish") version "3.0.1"

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

        gradleVersion = "7.5"
        distributionType = ALL
        distributionSha256Sum = "97a52d145762adc241bad7fd18289bf7f6801e08ece6badf80402fe2b9f250b1"
    }

    val dokkaOutputDir = "${rootProject.projectDir}/dokka"

    clean {
        delete(dokkaOutputDir)
    }

    dokkaHtmlMultiModule.configure {
        dependsOn(clean)
        outputDirectory.set(file(dokkaOutputDir))
    }

    gitPublishReset {
        dependsOn(dokkaHtmlMultimodule)
    }
}

configure<GitPublishExtension> {
    repoUri.set("https://github.com/kordlib/kord.git")
    branch.set("gh-pages")

    contents {
        from(project.projectDir.resolve("dokka"))
    }

    commitMessage.set("Update Docs")
}
