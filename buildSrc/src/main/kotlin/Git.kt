import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal fun Project.git(vararg command: String) = providers.exec {
        commandLine("git", *command)
        workingDir = rootDir
}.standardOutput.asText.map { it.trim() }.get()
