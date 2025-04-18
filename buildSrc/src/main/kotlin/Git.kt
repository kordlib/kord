import org.gradle.api.Project

internal fun Project.git(vararg command: String) = providers.exec {
        commandLine("git", *command)
        workingDir = rootDir
}.standardOutput.asText.map { it.trim() }.get()
