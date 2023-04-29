import org.gradle.api.Project

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"
}

val Project.libraryVersion: String
    get() {
        val tag = System.getenv("GITHUB_TAG_NAME").takeUnless { it.isNullOrBlank() }
        return tag ?: "${git("branch", "--show-current").replace('/', '-')}-SNAPSHOT"
    }

val Project.commitHash get() = git("rev-parse", "--verify", "HEAD")
val Project.shortCommitHash get() = git("rev-parse", "--short", "HEAD")

val Project.isRelease get() = !libraryVersion.endsWith("-SNAPSHOT")

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
