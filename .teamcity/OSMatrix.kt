import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Id

/**
 * Creates a matrix for [operatingSystems] and runs the build everywhere.
 *
 * @param name a base name for all [types][BuildType]
 *              (Is converted to `$name ($operatingSystem)`
 * @param id a base [Id] for all [types][BuildType]
 *              (Is converted to `$name_$operatingSystem)
 * @param operatingSystems [List] of operating systems
 * @param configure configuration for all types
 */
@Suppress("FunctionName")
fun MultiOSKordBuild(
    name: String,
    id: Id,
    operatingSystems: List<String> = listOf("Linux", "Windows", "Mac OS"),
    configure: BuildType.() -> Unit
): List<BuildType> {
    return operatingSystems.map { operatingSystem ->
        KordBuild("$name ($operatingSystem)") {
            this.id = AbsoluteId("${id.value}_${operatingSystem.replace(' ', '_').lowercase()}")
            requirements {
                matches("teamcity.agent.jvm.os.family", "Linux")
            }

            configure()
        }
    }
}
