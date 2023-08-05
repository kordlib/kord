plugins {
    // For some reason, Gradle does not generate an accessor for this
    id("com.vanniktech.maven.publish.base")
}

group = Library.group
version = libraryVersion

mavenPublishing {
    coordinates(Library.group, "kord-${project.name}", libraryVersion)
    // This sets up OSSRH snapshots + maven central
    publishToMavenCentral(automaticRelease = true)
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
                distribution = "https://github.com/kordlib/kord/blob/LICENSE"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/kordlib/kord.git"
            developerConnection = "scm:git:ssh://git@github.com:kordlib/kord.git"
            url = Library.projectUrl
        }
    }
}
