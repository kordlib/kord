import org.gradle.api.Project

/**
 * whether the process has been invoked by JitPack
 */
val isJitPack get() = "true" == System.getenv("JITPACK")

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"
}


val Project.libraryVersion: String
    get() = if (isJitPack) {
        System.getenv("RELEASE_TAG")
    } else {
        val tag = System.getenv("GITHUB_TAG_NAME").takeUnless { it.isNullOrBlank() }
        tag ?: "${git("branch", "--show-current").replace('/', '-')}-SNAPSHOT"
    }

val Project.commitHash get() = git("rev-parse", "HEAD")
val Project.shortCommitHash get() = git("rev-parse", "--short", "HEAD")

val Project.isSnapshot get() = libraryVersion.endsWith("-SNAPSHOT")
val Project.isRelease get() = !isSnapshot

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
