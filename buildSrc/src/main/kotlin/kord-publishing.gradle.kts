import java.util.Base64

plugins {
    `maven-publish`
    signing
}

tasks {
    publishing {
        publications {
            create<MavenPublication>(Library.name) {
                groupId = Library.group
                artifactId = "kord-${project.name}"
                version = Library.version

                pom {
                    name.set(Library.name)
                    description.set(Library.description)
                    url.set(Library.description)

                    organization {
                        name.set("Kord")
                        url.set("https://github.com/kordlib")
                    }

                    developers {
                        developer {
                            name.set("The Kord Team")
                        }
                    }

                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/kordlib/kord/issues")
                    }

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("http://opensource.org/licenses/MIT")
                        }
                    }
                    scm {
                        connection.set("scm:git:ssh://github.com/kordlib/kord.git")
                        developerConnection.set("scm:git:ssh://git@github.com:kordlib/kord.git")
                        url.set(Library.projectUrl)
                    }
                }

                if (!isJitPack) {
                    repositories {
                        maven {
                            url = if (Library.isSnapshot) uri(Repo.snapshotsUrl)
                            else uri(Repo.releasesUrl)

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
}
