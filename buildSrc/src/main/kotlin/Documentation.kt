import org.gradle.kotlin.dsl.assign
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URI

fun AbstractDokkaLeafTask.applyKordDokkaOptions() {

    moduleName = "kord-${project.name}"

    failOnWarning = true

    dokkaSourceSets.configureEach {

        jdkVersion = Jvm.target

        suppressGeneratedFiles = false

        sourceLink {
            localDirectory = project.projectDir
            remoteUrl = URI("https://github.com/kordlib/kord/blob/${project.commitHash}/${project.name}").toURL()
            remoteLineSuffix = "#L"
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
            matchingRegex = """com\.iwebpp\.crypto"""
            suppress = true
        }
    }
}
