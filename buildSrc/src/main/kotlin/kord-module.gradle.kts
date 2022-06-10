import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu")
    `maven-publish`
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly(kotlin("test-junit5"))
}

kotlin {
    explicitApi()
}

tasks {
    tasks.getByName("apiCheck") {
        onlyIf { Library.isRelease }
    }

    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Jvm.target
            freeCompilerArgs = listOf(
                CompilerArguments.coroutines,
                CompilerArguments.time,
                CompilerArguments.contracts,
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
        publications.withType<MavenPublication> {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(dokkaJar.get())
        }
    }

    java {
        // We don't use java, but this prevents a Gradle warning,
        // telling you to target the same java version for java and kt
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}
