import java.lang.System.getenv
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
            version = libraryVersion

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

    repositories {
        maven {
            url = uri(if (isRelease) Repo.releasesUrl else Repo.snapshotsUrl)

            credentials {
                username = getenv("NEXUS_USER")
                password = getenv("NEXUS_PASSWORD")
            }
        }
    }
}

signing {
    val secretKey = getenv("SIGNING_KEY")?.let { String(Base64.getDecoder().decode(it)) }
    val password = getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(secretKey, password)
    //sign(publishing.publications)
}
