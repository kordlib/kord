import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.apache.commons.codec.binary.Base64

plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin apply false
    `kotlinx-atomicfu` apply false
    `kotlinx-binary-compatibility-validator` apply false
    id("org.jetbrains.dokka") version "1.4.20"
    id("org.ajoberstar.git-publish") version "2.1.3"

    signing
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.22.0"
}

apply(plugin = "binary-compatibility-validator")

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

repositories {
    mavenLocal()
}

group = Library.group
version = Library.version

subprojects {
    repositories {
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
    }

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "maven-publish")
    apply(plugin = "kotlinx-atomicfu")
    apply(plugin = "org.jetbrains.dokka")

    if (!isJitPack && Library.isRelease) {
        apply(plugin = "signing")
    }

    dependencies {
        api(Dependencies.`kotlinx-serialization`)
        implementation(Dependencies.`kotlinx-serialization-json`)
        api(Dependencies.`kotlinx-coroutines`)
        implementation(Dependencies.`kotlinx-atomicfu`)
        implementation(Dependencies.`kotlin-logging`)

        testImplementation(Dependencies.`kotlin-test`)
        testImplementation(Dependencies.junit5)
        testImplementation(Dependencies.`junit-jupiter-api`)
        testRuntimeOnly(Dependencies.`junit-jupiter-engine`)
        testImplementation(Dependencies.`kotlinx-coroutines-test`)
        testRuntimeOnly(Dependencies.`kotlin-reflect`)
        testRuntimeOnly(Dependencies.sl4j)
    }

    tasks.getByName("apiCheck").onlyIf { Library.isStableApi }

    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
    compileKotlin.kotlinOptions.jvmTarget = Jvm.target


    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines.plusAssign("junit-jupiter")
        }
    }


    tasks.dokkaHtml.configure {
        this.outputDirectory.set(file("${project.projectDir}/dokka/kord/"))

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


    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val dokkaJar by tasks.registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        from(tasks.dokkaHtml)
        dependsOn(tasks.dokkaHtml)
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

                    withXml {
                        val repoNode = asNode().appendNode("repositories").appendNode("repository")

                        with(repoNode) {
                            appendNode("id", "jcenter")
                            appendNode("name", "jcenter-bintray")
                            appendNode("url", "https://jcenter.bintray.com")
                        }
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
                useInMemoryPgpKeys(String(Base64().decode(signingKey.toByteArray())),
                    signingPassword)
            }
            sign(publishing.publications[Library.name])
        }
    }
}

tasks {
    val dokkaOutputDir = "${rootProject.projectDir}/dokka"

    val clean = getByName("clean", Delete::class) {
        delete(rootProject.buildDir)
        delete(dokkaOutputDir)
    }

    dokkaHtmlMultiModule {
        dependsOn(clean)
        outputDirectory.set(file(dokkaOutputDir))
//        documentationFileName.set("DokkaDescription.md")
    }

    val fixIndex by register<Copy>("fixIndex") {
        dependsOn(dokkaHtmlMultiModule)
        val outputDirectory = dokkaHtmlMultiModule.get().outputDirectory.get()
        from(outputDirectory)
        include("-modules.html")
        into(outputDirectory)

        rename("-modules.html", "index.html")
    }

    gitPublishReset {
        dependsOn(fixIndex)
    }
}

configure<GitPublishExtension> {
    repoUri.set("https://github.com/kordlib/kord.git")
    branch.set("gh-pages")

    contents {
        from(file("${project.projectDir}/dokka"))
    }

    commitMessage.set("Update Docs")
}
