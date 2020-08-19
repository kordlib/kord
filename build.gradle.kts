import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
//import org.jetbrains.dokka.gradle.DokkaTask
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.ajoberstar.gradle.git.publish.tasks.GitPublishPush

buildscript {
    repositories {
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${Versions.atomicFu}")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin
    id("org.jetbrains.dokka") version "1.4.0-M3-dev-61"
    id("org.ajoberstar.git-publish") version "2.1.3"
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
    mavenCentral()
    jcenter()
    mavenLocal()
}

dependencies {
    api(Dependencies.jdk8)
    dokkaPlugins("org.jetbrains.dokka:dokka-base:0.11.0-SNAPSHOT")
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

//tasks {
//    val dokkaOutputDir = "dokka"
//
//    val clean = getByName("clean", Delete::class) {
//        delete(rootProject.buildDir)
//        delete(dokkaOutputDir)
//    }
//
//    val dokka by getting(DokkaTask::class) {
//        dependsOn(clean)
//        outputDirectory = dokkaOutputDir
//        outputFormat = "html"
//        subProjects = listOf("core", "rest", "gateway", "common")
//    }
//
//    val fixIndex by register<DocsTask>("fixIndex") {
//        dependsOn(dokka)
//    }
//
//    val gitPublishPush by getting(GitPublishPush::class) {
//        dependsOn(fixIndex)
//    }
//}

//configure<GitPublishExtension> {
//    repoUri.set("https://github.com/kordlib/kord.git")
//    branch.set("gh-pages")
//
//    contents {
//        from("dokka")
//    }
//
//    commitMessage.set("Update Docs")
//}
