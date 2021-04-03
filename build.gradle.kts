import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.ajoberstar.gradle.git.publish.tasks.GitPublishReset
import org.apache.commons.codec.binary.Base64

buildscript {
    repositories {
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        //https://github.com/melix/japicmp-gradle-plugin/issues/36
        classpath("com.google.guava:guava:28.2-jre")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${Versions.atomicFu}")
        classpath("org.jetbrains.kotlinx:binary-compatibility-validator:${Versions.binaryCompatibilityValidator}")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin
    id("org.jetbrains.dokka") version "1.4.0"
    id("org.ajoberstar.git-publish") version "2.1.3"
    id("com.jfrog.bintray") version "1.8.5"

    signing
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.22.0"
}

apply(plugin = "binary-compatibility-validator")

repositories {
    mavenCentral()
    jcenter()
    mavenLocal()
}

dependencies {
    api(Dependencies.jdk8)
}

group = Library.group
version = Library.version

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "com.jfrog.bintray")
    apply(plugin = "maven-publish")
    apply(plugin = "kotlinx-atomicfu")
    apply(plugin = "org.jetbrains.dokka")
  
    if(!isJitPack && Library.isRelease){
        apply(plugin = "signing")
    }

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url ="https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
    }

    dependencies {
        api(Dependencies.jdk8)
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

    apply<BintrayPlugin>()

    publishing {
        publications {
            create<MavenPublication>(Library.name) {
                assert(Library.version != "undefined") { "No release/snapshot version found." }
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
                useInMemoryPgpKeys(String(Base64().decode(signingKey.toByteArray())), signingPassword)
            }
            sign(publishing.publications[Library.name])
        }
    }

    configure<BintrayExtension> {
        user = System.getenv("BINTRAY_USER")
        key = System.getenv("BINTRAY_KEY")
        setPublications(Library.name)
        publish = true

        pkg = PackageConfig().apply {
            repo = "Kord"
            name = "Kord"
            userOrg = "kordlib"
            setLicenses("MIT")
            vcsUrl = "https://github.com/kordlib/kord.git"
            websiteUrl = "https://github.com/kordlib/kord.git"
            issueTrackerUrl = "https://github.com/kordlib/kord/issues"

            version = VersionConfig().apply {
                name = Library.version
                desc = Library.description
                vcsTag = Library.version
            }
        }
    }
}

tasks {
    val dokkaOutputDir = "${rootProject.projectDir}/dokka"

    val clean = getByName("clean", Delete::class) {
        delete(rootProject.buildDir)
        delete(dokkaOutputDir)
    }

    dokkaHtmlMultiModule.configure {
        dependsOn(clean)
        outputDirectory.set(file(dokkaOutputDir))
        documentationFileName.set("DokkaDescription.md")
    }


    val fixIndex by register<DocsTask>("fixIndex") {
        dependsOn(dokkaHtmlMultimodule)
    }

    val gitPublishReset by getting(GitPublishReset::class) {
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

nexusStaging { }
