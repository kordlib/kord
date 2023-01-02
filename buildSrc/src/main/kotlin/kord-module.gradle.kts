import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    //`kotlinx-atomicfu`
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
    `maven-publish`
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    sourceSets {
        // mark ksp src dir
        main { kotlin.srcDir("build/generated/ksp/main/kotlin") }

        // allow `ExperimentalCoroutinesApi` for `runTest {}`
        test { languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi") }
    }
}

// https://github.com/Kotlin/kotlinx-atomicfu/issues/210
//atomicfu {
//    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
//    dependenciesVersion = libs.findVersion("kotlinx-atomicfu").get().requiredVersion
//}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = Jvm.targetString
        targetCompatibility = Jvm.targetString
    }

    withType<KotlinCompile> {
        kotlinOptions {
            applyKordKotlinOptions()
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

        // make sure ksp generates files before building docs
        dependsOn(compileKotlin)

        failOnWarning.set(true)

        dokkaSourceSets.configureEach {

            jdkVersion.set(Jvm.targetInt)

            val baseRemoteUrl =
                "https://github.com/kordlib/kord/blob/${Library.commitHashOrDefault("0.8.x")}/${project.name}"

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("$baseRemoteUrl/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            // config for files generated by ksp
            suppressGeneratedFiles.set(false)
            sourceLink {
                // will fail if dir doesn't exist -> always create it, won't harm if not needed
                localDirectory.set(file("build/generated/ksp/main/kotlin").apply { mkdirs() })
                remoteUrl.set(URL("$baseRemoteUrl/build/generated/ksp/main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink("https://kotlinlang.org/api/kotlinx.coroutines/")
            externalDocumentationLink("https://kotlinlang.org/api/kotlinx.serialization/")
            externalDocumentationLink(
                url = "https://kotlinlang.org/api/kotlinx-datetime/",
                packageListUrl = "https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/package-list",
            )
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
