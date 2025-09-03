plugins {
    com.vanniktech.maven.publish
    signing
}

mavenPublishing {
    coordinates(Library.group, "kord-${project.name}", libraryVersion.get())

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

//    repositories {
////        if (!isRelease) {
////            maven {
////                name = "kordSnapshots"
////                url = uri("https://repo.kord.dev/snapshots")
////                credentials {
////                    username = getenv("KORD_REPO_USER")
////                    password = getenv("KORD_REPO_PASSWORD")
////                }
////            }
////        }
//    }
}
