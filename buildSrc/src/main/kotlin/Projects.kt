import org.gradle.api.Project
import org.gradle.api.provider.Provider

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"
}

private val Project.tag
    get() = git("tag", "--no-column", "--points-at", "HEAD")
        .map {
            it.takeIf { it.isNotBlank() }
            ?.lines()
            ?.single()
        }

val Project.libraryVersion: Provider<String>
    get() {
        val snapshotVersion = git("branch", "--show-current").map { branch ->
            val snapshotPrefix = when (branch) {
                "main" -> providers.gradleProperty("nextPlannedVersion").get()
                else -> branch.replace('/', '-')
            }
            "$snapshotPrefix-SNAPSHOT"
        }

        return tag.orElse(snapshotVersion)
    }

val Project.commitHash get() = git("rev-parse", "--verify", "HEAD")
val Project.shortCommitHash get() = git("rev-parse", "--short", "HEAD")

val Project.isRelease get() = tag.isPresent

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
