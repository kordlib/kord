import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Paths

open class DocsTask : DefaultTask() {

    @TaskAction
    fun publish() {
        val dokkaRoot = Paths.get(project.projectDir.absolutePath, "dokka")

        val modulesFile = Paths.get(dokkaRoot.toAbsolutePath().toString(), "-modules.html")
        val newIndex = Paths.get(dokkaRoot.toAbsolutePath().toString(), "index.html")

        Files.copy(modulesFile, newIndex)
    }

}
