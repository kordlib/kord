import org.gradle.api.Project
import java.io.ByteArrayOutputStream

/**
 * whether the process has been invoked by JitPack
 */
val isJitPack get() = "true" == System.getenv("JITPACK")

val Project.commitHash: String
    get() = try {
        ByteArrayOutputStream().use { out ->
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = out
            }
            out.toString().trim()
        }
    } catch (e: Throwable) {
        System.getenv("GITHUB_SHA") ?: "unknown"
    }

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    val version: String
        get() = if (isJitPack) System.getenv("RELEASE_TAG")
        else {
            val tag = System.getenv("GITHUB_TAG_NAME")
            val branch = System.getenv("GITHUB_BRANCH_NAME")
            when {
                !tag.isNullOrBlank() -> tag
                !branch.isNullOrBlank() && branch.startsWith("refs/heads/") ->
                    branch.substringAfter("refs/heads/").replace("/", "-") + "-SNAPSHOT"
                else -> "undefined"
            }

        }

    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"

    val isSnapshot: Boolean get() = version.endsWith("-SNAPSHOT")

    /**
     * Whether the current API is considered stable, and should be compared to the 'golden' API dump.
     */
    val isRelease: Boolean get() = !isSnapshot && !isUndefined

    val isUndefined get() = version == "undefined"
}

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
