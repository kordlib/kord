import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URL

fun TaskContainer.configureDokka(additionalConfiguration: AbstractDokkaLeafTask.() -> Unit = {}) {
    withType<AbstractDokkaLeafTask>().configureEach {

        failOnWarning.set(true)

        dokkaSourceSets.configureEach {

            jdkVersion.set(Jvm.target)

            suppressGeneratedFiles.set(false)

            sourceLink {
                localDirectory.set(project.projectDir)
                remoteUrl.set(URL("https://github.com/kordlib/kord/blob/${Library.commitHashOrDefault("0.9.x")}/${project.name}"))
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
