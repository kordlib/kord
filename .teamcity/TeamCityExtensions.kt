import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.Triggers
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/**
 * Short-hand for only VCS triggers.
 */
fun Triggers.vcs() = vcs {  }

/**
 * Adds all [types] to this [Project].
 *
 * @see MultiOSKordBuild
 */
fun Project.buildType(types: Iterable<BuildType>) = types.forEach(::buildType)
