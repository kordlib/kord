import org.ajoberstar.grgit.Grgit

/**
 * whether the process has been invoked by JitPack
 */
val isJitPack get() = "true" == System.getenv("JITPACK")

private

object Library {
    private val git = Grgit.open()
    private val head = git.head()
    const val name = "kord"
    const val group = "dev.kord"

    val version: String
        get() = if (isJitPack) System.getenv("RELEASE_TAG")
        else {
            val tag = git.tag.list().firstOrNull {
                it.commit == head
            }
            val branch = git.branch.current().name
            when {
                tag != null -> tag.name
                !branch.isNullOrBlank() -> branch.replace("/", "-") + "-SNAPSHOT"
                else -> "undefined"
            }

        }

    val commitHash get() = head.id

    val shortCommitHash get() = head.abbreviatedId

    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"

    val isSnapshot: Boolean get() = version.endsWith("-SNAPSHOT")

    val isRelease: Boolean get() = !isSnapshot && !isUndefined

    val isUndefined get() = version == "undefined"
}

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
