import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URL

fun TaskContainer.configureDokka(additionalConfiguration: AbstractDokkaLeafTask.() -> Unit = {}) {
    withType<AbstractDokkaLeafTask>().configureEach {
        // see https://kotlin.github.io/dokka/<dokka version>/user_guide/gradle/usage/#configuration-options

        // include documentation generated by ksp
        dependsOnKspKotlin()

        failOnWarning.set(true)

        dokkaSourceSets.configureEach {

            jdkVersion.set(Jvm.target)

            val baseRemoteUrl =
                "https://github.com/kordlib/kord/blob/${Library.commitHashOrDefault("0.8.x")}/${project.name}"

            sourceLink {
                localDirectory.set(project.file("src/main/kotlin"))
                remoteUrl.set(URL("$baseRemoteUrl/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            // config for files generated by ksp
            suppressGeneratedFiles.set(false)
            sourceLink {
                // will fail if dir doesn't exist -> always create it, won't harm if not needed
                localDirectory.set(project.file("build/generated/ksp/main/kotlin").apply { mkdirs() })
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

        additionalConfiguration()
    }
}