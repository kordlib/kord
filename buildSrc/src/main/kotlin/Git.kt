import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.io.ByteArrayOutputStream

internal fun Project.git(vararg command: String): Provider<String> {
    return providers.exec {
        commandLine("git", *command)
        workingDir = rootDir
    }.standardOutput.asText.map { it.trim() }
}
