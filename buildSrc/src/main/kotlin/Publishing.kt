import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

open class DocsTask : DefaultTask() {

    @TaskAction
    fun publish() {
        val dokkaRoot = Paths.get(project.projectDir.absolutePath, "dokka")

        val index = Paths.get(dokkaRoot.toAbsolutePath().toString(), "kord", "index.html")
        val newIndex = Paths.get(dokkaRoot.toAbsolutePath().toString(), "index.html")

        //We're technically not moving the index.html. Instead, a working copy will be brought over to the root directory.
        //This is a bit of a MacGyver move, but it's the easiest solution I could think of.
        Files.createFile(newIndex)

        var reachedPackages = false
        //the default index.html is nested inside a folder, we'll move all links up by one directory to fix this in the
        //root index.html copy.
        Files.lines(index).map { it.replace("""../""", "") }.forEach {
            var line = it
            if ( it.contains("packages", true)) {
                reachedPackages = true
            }

            if (reachedPackages){
                //once we reach the packages declaration, we'll have to do a similar thing as above
                //we'll move all package links down into the kord directory.
                line = line.replace("""href="""", """href="kord/""")
            }

            Files.write(newIndex, (line + "\n").toByteArray(Charsets.UTF_8), StandardOpenOption.APPEND)
        }
    }

}
