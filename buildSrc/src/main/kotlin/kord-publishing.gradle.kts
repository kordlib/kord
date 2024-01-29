import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    id("com.vanniktech.maven.publish.base")
    dev.kord.`gradle-tools`
}

kord {
    publicationName = "mavenCentral"
    metadataHost = KonanTarget.MACOS_ARM64
}

mavenPublishing {
    coordinates(Library.group, "cache-${project.name}")
    publishToMavenCentral()
    signAllPublications()

    pom {
        name = Library.name
        description = Library.description
        url = Library.projectUrl

        organization {
            name = "Kord"
            url = "https://github.com/kordlib"
        }

        developers {
            developer {
                name = "The Kord Team"
            }
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/kordlib/kord/issues"
        }

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/kordlib/kord.git"
            developerConnection = "scm:git:ssh://git@github.com:kordlib/kord.git"
            url = Library.projectUrl
        }
    }
}
