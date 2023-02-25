import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

fun Task.dependsOnKspKotlin(name: String = "kspKotlin") {
    val kspKotlin = project.tasks.findByName(name)
    if (kspKotlin != null) dependsOn(kspKotlin)
}
