import jetbrains.buildServer.configs.kotlin.Triggers
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/**
 * Short-hand for only VCS triggers.
 */
fun Triggers.vcs() = vcs {  }
