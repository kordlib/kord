import org.gradle.api.Project
import java.io.ByteArrayOutputStream

internal fun Project.git(vararg command: String): String {
    val output = ByteArrayOutputStream()
    exec {
        commandLine("git", *command)
        standardOutput = output
        errorOutput = output
        workingDir = rootDir
    }.rethrowFailure().assertNormalExitValue()
    return output.toString().trim()
}
