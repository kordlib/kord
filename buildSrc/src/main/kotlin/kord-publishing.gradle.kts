import java.util.Base64

plugins {
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>(Library.name) {
            groupId = Library.group
            artifactId = "kord-${project.name}"
            version = Library.version

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
                        url = "http://opensource.org/licenses/MIT"
                    }
                }

                scm {
                    connection = "scm:git:ssh://github.com/kordlib/kord.git"
                    developerConnection = "scm:git:ssh://git@github.com:kordlib/kord.git"
                    url = Library.projectUrl
                }
            }

            if (!isJitPack) {
                repositories {
                    maven {
                        url = uri(if (Library.isSnapshot) Repo.snapshotsUrl else Repo.releasesUrl)

                        credentials {
                            username = System.getenv("NEXUS_USER")
                            password = System.getenv("NEXUS_PASSWORD")
                        }
                    }
                }
            }
        }
    }
}

if (!isJitPack && Library.isRelease) {
    signing {
        val signingKey = findProperty("signingKey")?.toString()
        val signingPassword = findProperty("signingPassword")?.toString()
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(String(Base64.getDecoder().decode(signingKey)), signingPassword)
        }
        sign(publishing.publications[Library.name])
    }
}
