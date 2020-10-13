import javassist.CtClass
import me.champeau.gradle.japicmp.JapicmpTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

open class BinaryCompatibilityExtension {
    var disableRootProject: Boolean = false
    val ignoreProjects: MutableList<String> = mutableListOf()
    val includeProjects: MutableList<String> = mutableListOf()
}

class BinaryCompatibilityPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<BinaryCompatibilityExtension>("binaryCompatibility")

        target.gradle.projectsEvaluated {

            target.tasks.register<ApiDumpTask>("dumpApi") {
                dependsOn("jar")
                configure(extension)
            }

            target.allprojects {
                with(extension) {
                    if (disableRootProject && rootProject == this@allprojects) return@allprojects
                    if (includeProjects.isNotEmpty() && name !in includeProjects) return@allprojects
                    if (ignoreProjects.isNotEmpty() && name in ignoreProjects) return@allprojects
                }

                val printReport = tasks.create("print") {
                    doFirst {
                        val report = file("$buildDir/reports/japi.txt")
                        val text = report.readText()
                        if (text.contains("No changes.")) {
                            return@doFirst
                        }

                        project.logger.error(text)
                    }

                }

                val binaryCompat = tasks.register<JapicmpTask>("binaryCheck") {
                    dependsOn("jar")
                    finalizedBy(printReport)

                    oldClasspath = fetchDumpedApi(this@allprojects)
                    val jar = tasks.getByName("jar") as Jar
                    newClasspath = target.files(jar.archiveFile)


                    isIncludeSynthetic = true
                    isFailOnModification = true
                    isIgnoreMissingClasses = true
                    isOnlyBinaryIncompatibleModified = true
                    txtOutputFile = file("$buildDir/reports/japi.txt")

                    class IgnoreInlinedLambdaFilter : japicmp.filter.ClassFilter {
                        override fun matches(classBinary: CtClass): Boolean {
                            return classBinary.name.contains("inlined")
                        }
                    }

                    class IgnoreGenerated : japicmp.filter.ClassFilter {

                        //ClassName$lowerCaseMethodName$optionalNumber
                        val generatedNestedClass = Regex(pattern = """\w+(\$\p{Lower}\w*)+(\$\d+)*""")
                        override fun matches(classBinary: CtClass): Boolean {
                            return when {
                                classBinary.name.any { it.isDigit() } -> true
                                classBinary.name.matches(generatedNestedClass) -> true
                                else -> false
                            }
                        }
                    }

                    addExcludeFilter(IgnoreInlinedLambdaFilter::class.java)
                    addExcludeFilter(IgnoreGenerated::class.java)

                }

                rootProject.tasks.getByName("check") {
                    dependsOn(binaryCompat)
                }

            }

        }


    }


    fun fetchDumpedApi(project: Project): FileCollection {
        val task = project.tasks.getByName("jar") as Jar
        val jarFile = task.archiveFile.get()

        val destination = Paths.get(project.projectDir.path, "api", jarFile.asFile.name)
        return project.files(destination)
    }

}

open class ApiDumpTask : DefaultTask() {

    @get:Input
    @set:Option(option = "tag", description = """
The version of the API to dump. 
Use `$CURRENT_FLAG` to dump the current, local version of the API. 
Use `$LATEST_FLAG` to dump the latest published version of the API.
`$LATEST_FLAG` by default.
"""
    )
    var version: String = LATEST_FLAG

    private var acceptProjectsPredicate: Project.() -> Boolean = { true }
    private var denyProjectsPredicate: Project.() -> Boolean = { false }
    private var ignoreRootProject: Boolean = false

    @TaskAction
    fun dumpApi() {
        project.allprojects {
            if (ignoreRootProject && this.rootProject == this) return@allprojects

            if (!acceptProjectsPredicate()) return@allprojects
            if (denyProjectsPredicate()) return@allprojects

            val jarFile = when (this@ApiDumpTask.version) {
                LATEST_FLAG -> {
                    val releaseVersion = getReleaseVersion("+")
                    getPublishedJar(this, releaseVersion)
                }
                CURRENT_FLAG -> {
                    val jarTask = project.tasks.getByName("jar") as Jar
                    jarTask.archiveFile.get().asFile
                }
                else -> {
                    val releaseVersion = getReleaseVersion(this@ApiDumpTask.version)
                    getPublishedJar(this, releaseVersion)
                }
            }

            val destination = Paths.get(project.projectDir.path, "api", "${this.name}.jar")
            Files.deleteIfExists(destination)

            Files.copy(jarFile.toPath(), destination)
        }

    }

    private fun getReleaseVersion(version: String): String {
        /**
         * There's probably a better way to do this, but it works.
         * We create a custom configuration, which we'll delete afterwards, to
         * resolve a dependency on kord-core with the `+` version.
         */
        val configurationName = "resolve-release-version"
        val configuration = project.configurations.create(configurationName)
        val dependency = project.dependencies.create("com.gitlab.kordlib.kord:kord-core:$version")
        project.dependencies.add(configurationName, dependency)

        val lenient = configuration.resolvedConfiguration.lenientConfiguration
        val resolvedDependency = lenient.firstLevelModuleDependencies
                .first { "com.gitlab.kordlib.kord:kord-core:" in it.name }

        project.configurations.remove(configuration)

        return resolvedDependency.moduleVersion
    }

    private fun getPublishedJar(project: Project, version: String): File {
        val oldGroup = project.group
        val group = Library.group
        val artifactId = "kord-${project.name}"

        try {
            val jarFile = "$artifactId-$version.jar"
            project.group = "virtual_group_for_japicmp" // Prevent it from resolving the current version.
            val dependency = project.dependencies.create("$group:$artifactId:$version@jar")
            return project.configurations.detachedConfiguration(dependency).files
                    .find { (it.name == jarFile) } ?: error("$dependency not found")
        } finally {
            project.group = oldGroup
        }
    }

    fun configure(extension: BinaryCompatibilityExtension) {
        ignoreRootProject = extension.disableRootProject

        if (extension.ignoreProjects.isNotEmpty()) {
            denyProjectsPredicate = { name !in extension.ignoreProjects }
        }

        if (extension.includeProjects.isNotEmpty()) {
            acceptProjectsPredicate = { name in extension.includeProjects }
        }
    }


    companion object {
        private const val LATEST_FLAG = "latest"
        private const val CURRENT_FLAG = "current"

    }


}
