import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project

/**
 * whether the process has been invoked by JitPack
 */
val isJitPack get() = "true" == System.getenv("JITPACK")

private var grgit: Grgit? = null
private val Project.git: Grgit
    get() {
        return grgit ?: Grgit.open {
            dir = rootProject.rootDir.absolutePath
        }.also {
            grgit = it
        }
    }

object Library {
    const val name = "kord"
    const val group = "dev.kord"

    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"
}

val Project.kordVersion: String
    get() = if (isJitPack) System.getenv("RELEASE_TAG")
    else {
        val head = git.head()
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

val Project.commitHash get() = git.head().id

val Project.shortCommitHash get() = git.head().abbreviatedId

val Project.isSnapshot: Boolean get() = kordVersion.endsWith("-SNAPSHOT")

val Project.isRelease: Boolean get() = !isSnapshot && !isUndefined

val Project.isUndefined get() = version == "undefined"

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
