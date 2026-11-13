import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.workers.ProcessIsolation

internal fun DokkaExtension.applyKordDokkaOptions(project: Project) {

    // Dokka runs out of memory with the default maxHeapSize when ProcessIsolation is used
    (dokkaGeneratorIsolation.get() as? ProcessIsolation)?.maxHeapSize = "1g"

    moduleName = "kord-${project.name}"

    dokkaPublications.configureEach {
        failOnWarning = true
    }

    dokkaSourceSets.configureEach {

        jdkVersion = 23

        suppressGeneratedFiles = false

        sourceLink {
            localDirectory = project.projectDir
            remoteUrl("https://github.com/kordlib/kord/blob/${project.commitHash}/${project.name}")
            remoteLineSuffix = "#L"
        }

        externalDocumentationLinks {
            register("kotlinx.coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
            }
            register("kotlinx.serialization") {
                url("https://kotlinlang.org/api/kotlinx.serialization/")
            }
            register("kotlinx-datetime") {
                url("https://kotlinlang.org/api/kotlinx-datetime/")
            }
            register("Ktor") {
                url("https://api.ktor.io/")
            }
        }

        // don't list `TweetNaclFast` in docs
        perPackageOption {
            matchingRegex = """com\.iwebpp\.crypto"""
            suppress = true
        }
    }
}
