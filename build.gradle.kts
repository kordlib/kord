import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.9.0"
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
        gradleVersion = "7.4.2"
        distributionType = ALL
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
