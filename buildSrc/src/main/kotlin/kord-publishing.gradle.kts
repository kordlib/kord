import gradle.kotlin.dsl.accessors._e5121a5856746b077c6819bbe5a86a2f.dokkaHtml
import java.util.Base64

plugins {
    `maven-publish`
    signing
}

val dokkaJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    publications {
        withType<MavenPublication> {
            artifact(dokkaJar)

            groupId = Library.group
            artifactId = "kord-${artifactId}"
            version = Library.version

            pom {
                name.set(Library.name)
                description.set(Library.description)
                url.set(Library.projectUrl)

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
