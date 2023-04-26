import org.gradle.api.Project

internal fun Project.git(vararg command: String): String {
    val process = ProcessBuilder("git", *command)
        .redirectErrorStream(true)
        .directory(rootDir)
        .start()
    val output = process.inputStream.reader().use { it.readText() }.trim()
    val exitStatus = process.waitFor()
    check(exitStatus == 0) { "git exited with status $exitStatus, output:\n$output" }
    return output
}
