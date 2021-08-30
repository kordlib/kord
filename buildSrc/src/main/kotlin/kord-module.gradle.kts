import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Base64

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu")

    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly(kotlin("test-junit5"))
}

tasks {
    tasks.getByName("apiCheck") {
        onlyIf { Library.isRelease }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Jvm.target
            freeCompilerArgs = listOf(
                CompilerArguments.coroutines,
                CompilerArguments.time,
                CompilerArguments.optIn
            )
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    dokkaHtml.configure {
        this.outputDirectory.set(project.projectDir.resolve("dokka").resolve("kord"))

        dokkaSourceSets {
            configureEach {
                platform.set(org.jetbrains.dokka.Platform.jvm)

                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(uri("https://github.com/kordlib/kord/tree/master/${project.name}/src/main/kotlin/").toURL())

                    remoteLineSuffix.set("#L")
                }

                jdkVersion.set(8)
            }
        }
    }

    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val dokkaHtml by getting

    val dokkaJar by registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        from(dokkaHtml)
        dependsOn(dokkaHtml)
    }

    withType<PublishToMavenRepository>().configureEach {
        doFirst { require(!Library.isUndefined) { "No release/snapshot version found." } }
    }

    publishing {
        publications {
            create<MavenPublication>(Library.name) {
                from(components["kotlin"])
                groupId = Library.group
                artifactId = "kord-${project.name}"
                version = Library.version

                artifact(sourcesJar.get())
                artifact(dokkaJar.get())

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
                            url.set("https://opensource.org/licenses/MIT")
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
                useInMemoryPgpKeys(Base64.getDecoder().decode(signingKey).toString(), signingPassword)
            }
            sign(publishing.publications[Library.name])
        }
    }
}
