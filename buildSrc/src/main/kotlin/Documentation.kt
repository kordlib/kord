import org.gradle.kotlin.dsl.assign
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URL

fun AbstractDokkaLeafTask.applyKordDokkaOptions() {
    failOnWarning = true

    dokkaSourceSets.configureEach {

        jdkVersion = Jvm.target

        suppressGeneratedFiles = false

        sourceLink {
            localDirectory = project.projectDir
            remoteUrl = URL("https://github.com/kordlib/kord/blob/${Library.commitHashOrDefault("0.9.x")}/${project.name}")
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
