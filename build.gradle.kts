import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaMultimoduleTask

import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.ajoberstar.gradle.git.publish.tasks.GitPublishPush

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
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${Versions.atomicFu}")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("org.ajoberstar.git-publish") version "2.1.3"
    id("me.champeau.gradle.japicmp") version "0.2.8"
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
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
    apply(plugin = "me.champeau.gradle.japicmp")

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://dl.bintray.com/kordlib/Kord")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
    }

    dependencies {
        api(Dependencies.jdk8)
        api(Dependencies.`kotlinx-serialization`)
        api(Dependencies.`kotlinx-coroutines`)
        implementation("org.jetbrains.kotlinx:atomicfu-jvm:${Versions.atomicFu}")
        implementation(Dependencies.`kotlin-logging`)

        testImplementation(Dependencies.`kotlin-test`)
        testImplementation(Dependencies.junit5)
        testImplementation(Dependencies.`junit-jupiter-api`)
        testRuntimeOnly(Dependencies.`junit-jupiter-engine`)
        testImplementation(Dependencies.`kotlinx-coroutines-test`)
        testRuntimeOnly(Dependencies.`kotlin-reflect`)
        testRuntimeOnly(Dependencies.sl4j)
    }

    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
    compileKotlin.kotlinOptions.jvmTarget = Jvm.target

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines.plusAssign("junit-jupiter")
        }
    }

    val japicmp by tasks.register<me.champeau.gradle.japicmp.JapicmpTask>("japicmp") {
        dependsOn(tasks.jar)

        fun baselineJar(project: Project, version: String): File {
            val oldGroup = project.group
            val group = Library.group
            val artifactId = "kord-${project.name}"

            try {
                val jarFile = "$artifactId-$version.jar"
                project.group = "virtual_group_for_japicmp" // Prevent it from resolving the current version.
                val dependency = project.dependencies.create("$group:$artifactId:$version@jar")
                return project.configurations.detachedConfiguration(dependency).files
                        .find { (it.name == jarFile) }.also {
                            println(it?.absolutePath)
                        } ?: error("$dependency not found")
            } finally {
                project.group = oldGroup
            }
        }

        oldClasspath = files(baselineJar(project, Versions.baselineVersion))
        newClasspath = files(tasks.jar.get().archiveFile)

        ignoreMissingClasses = true
        includeSynthetic = true
        onlyBinaryIncompatibleModified = true
        txtOutputFile = file("$buildDir/reports/japi.txt")
    }

    tasks.dokkaHtml.configure {
        outputDirectory = "${rootProject.projectDir}/dokka/kord/"
        dokkaSourceSets {
            configureEach {
                platform = org.jetbrains.dokka.Platform.jvm.name

                //doesn't work for whatever reason
                sourceLink {
                    val relativePath = project.projectDir.relativeTo(project.rootProject.projectDir).path
                    path = "$relativePath/src/main/kotlin"
                    url = "https://github.com/kordlib/kord/blob/master/${project.name}/src/main/kotlin"

                    lineSuffix = "#L"
                }

                jdkVersion = 8
            }
        }
    }


    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    apply<BintrayPlugin>()

    configure<PublishingExtension> {
        publications {
            register("kord", MavenPublication::class) {
                from(components["kotlin"])
                groupId = Library.group
                artifactId = "kord-${project.name}"
                version = Library.version

                artifact(sourcesJar.get())
            }
        }
    }

    configure<BintrayExtension> {
        user = System.getenv("BINTRAY_USER")
        key = System.getenv("BINTRAY_KEY")
        setPublications("kord")
        publish = true

        pkg = PackageConfig().apply {
            repo = "Kord"
            name = "Kord"
            userOrg = "kordlib"
            setLicenses("MIT")
            vcsUrl = "https://gitlab.com/kordlib/kord.git"
            websiteUrl = "https://gitlab.com/kordlib/kord.git"
            issueTrackerUrl = "https://gitlab.com/kordlib/kord/issues"

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

    dokkaHtmlMultimodule.configure {
        dependsOn(clean)
        outputDirectory = dokkaOutputDir
        documentationFileName = "DokkaDescription.md"
    }


    val fixIndex by register<DocsTask>("fixIndex") {
        dependsOn(dokkaHtmlMultimodule)
    }

    val gitPublishPush by getting(GitPublishPush::class) {
        dependsOn(fixIndex)
    }

}

configure<GitPublishExtension> {
    repoUri.set("https://github.com/kordlib/kord.git")
    branch.set("gh-pages")

    contents {
        from("dokka")
    }

    commitMessage.set("Update Docs")
}
