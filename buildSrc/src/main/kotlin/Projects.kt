import org.gradle.api.Project

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"
}

private val Project.tag
    get() = git("tag", "--no-column", "--points-at", "HEAD")
        .takeIf { it.isNotBlank() }
        ?.lines()
        ?.single()

val Project.libraryVersion
    get() = tag ?: run {
        val snapshotPrefix = when (val branch = git("branch", "--show-current")) {
            "main" -> providers.gradleProperty("nextPlannedVersion").get()
            else -> branch.replace('/', '-')
        }
        "$snapshotPrefix-SNAPSHOT"
    }

val Project.commitHash get() = git("rev-parse", "--verify", "HEAD")
val Project.shortCommitHash get() = git("rev-parse", "--short", "HEAD")

val Project.isRelease get() = tag != null

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
