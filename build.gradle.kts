import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin

buildscript {
    repositories {
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies { api(Dependencies.jdk8) }

group = Library.group
version = Library.version

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "com.jfrog.bintray")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://dl.bintray.com/kordlib/Kord")
    }

    dependencies {
        api(Dependencies.jdk8)
        api(Dependencies.`kotlinx-serialization`)
        api(Dependencies.`kotlinx-coroutines`)
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

