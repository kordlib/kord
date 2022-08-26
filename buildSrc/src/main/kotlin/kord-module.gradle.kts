import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu")
    id("com.google.devtools.ksp")
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

    sourceSets.main {
        // mark ksp src dir
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }

    sourceSets.test {
        // allow ExperimentalCoroutinesApi for `runTest {}`
        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = Jvm.targetString
        targetCompatibility = Jvm.targetString
    }

    withType<KotlinCompile> {
        kotlinOptions {
            kordJvmOptions()
            freeCompilerArgs += listOf(
                CompilerArguments.time,
                CompilerArguments.contracts,

                CompilerArguments.kordPreview,
                CompilerArguments.kordExperimental,
                CompilerArguments.kordVoice,
            )
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    // configure both dokkaHtml and dokkaHtmlPartial tasks
    // (dokkaHtmlMultiModule depends on dokkaHtmlPartial, dokkaJar depends on dokkaHtml)
    withType<AbstractDokkaLeafTask> {
        // see https://kotlin.github.io/dokka/<dokka version>/user_guide/gradle/usage/#configuration-options

        failOnWarning.set(true)

        dokkaSourceSets.configureEach {

            jdkVersion.set(Jvm.targetInt)

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/kordlib/kord/blob/${Library.commitHashOrDefault("0.8.x")}/${project.name}/src/main/kotlin/"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink("https://kotlinlang.org/api/kotlinx.coroutines/")
            externalDocumentationLink("https://kotlinlang.org/api/kotlinx.serialization/")
            externalDocumentationLink("https://api.ktor.io/")

            // don't list `TweetNaclFast` in docs
            perPackageOption {
                matchingRegex.set("""com\.iwebpp\.crypto""")
                suppress.set(true)
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
}
