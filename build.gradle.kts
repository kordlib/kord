plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
    alias(libs.plugins.git.publish)
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

tasks {
    gitPublishPush {
        dependsOn(dokkaHtmlMultimodule)
    }
}

gitPublish {
    repoUri.set("https://github.com/DRSchlaubi/lavakord.git")
    branch.set("gh-pages")

    contents {
        from(file("docs"))
    }

    commitMessage.set("Update Docs")
}
