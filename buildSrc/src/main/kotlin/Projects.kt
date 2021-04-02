import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

val DependencyHandlerScope.common get() = project(":common")
val DependencyHandlerScope.rest get() = project(":rest")
val DependencyHandlerScope.gateway get() = project(":gateway")
val DependencyHandlerScope.core get() = project(":core")


/**
 * whether the process has been invoked by JitPack
 */
val isJitPack get() = "true" == System.getenv("JITPACK")

object Library {
    const val name = "kord"
    const val group = "dev.kord"
    val version: String = if (isJitPack) System.getenv("RELEASE_TAG")
    else System.getenv("GITHUB_TAG_NAME") ?: System.getenv("GITHUB_HEAD_REF") + "-SNAPSHOT"

    val isSnapshot: Boolean get() = version.endsWith("-SNAPSHOT")
    val isRelease: Boolean get() = !isSnapshot

    const val description = "Idiomatic Kotlin Wrapper for The Discord API"
    const val projectUrl = "https://github.com/kordlib/kord"

    /**
     * Whether the current API is considered stable, and should be compared to the 'golden' API dump.
     */
    val isStableApi: Boolean get() = !isSnapshot
}

object Repo {
    const val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    const val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
}
