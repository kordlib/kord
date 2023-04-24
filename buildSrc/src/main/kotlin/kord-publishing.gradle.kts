import java.util.Base64

plugins {
    `maven-publish`
    signing
}

fun MavenPublication.registerDokkaJar() =
    tasks.register<Jar>("${name}DokkaJar") {
        archiveClassifier = "javadoc"
        destinationDirectory = destinationDirectory.get().dir(name)
        from(tasks.named("dokkaHtml"))
    }

publishing {
    publications {
        withType<MavenPublication>().configureEach {
            if (project.name != "bom") artifact(registerDokkaJar())

            groupId = Library.group
            artifactId = "kord-$artifactId"
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

if (!isJitPack && Library.isRelease) {
    signing {
        val signingKey = findProperty("signingKey")?.toString()
        val signingPassword = findProperty("signingPassword")?.toString()
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(String(Base64.getDecoder().decode(signingKey)), signingPassword)
        }
        sign(publishing.publications)
    }
}
