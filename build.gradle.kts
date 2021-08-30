import org.ajoberstar.gradle.git.publish.GitPublishExtension

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.6.0"
    id("org.jetbrains.dokka")

    id("org.ajoberstar.git-publish") version "2.1.3"

    signing
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.22.0"
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

tasks {
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
