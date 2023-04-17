import jetbrains.buildServer.configs.kotlin.BuildSteps
import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle

const val debugParamName = "debug"
const val gradleHome = ".gradle-home"
const val gradleHomeFlag = "--gradle-user-home $gradleHome"

/**
 * Adds a new [Gradle build step][GradleBuildStep] that supports the debug parameter.
 */
fun BuildSteps.debuggableGradle(name: String, configure: GradleBuildStep.() -> Unit) {
    gradle {
        this.name = "$name (Debug)"
        conditions {
            equals(debugParamName, true.toString())
        }
        configure()
        gradleParams += listOf("-d", gradleHomeFlag)
    }

    gradle {
        this.name = name
        conditions {
            equals(debugParamName, false.toString())
        }
        configure()
        gradleParams += listOf(gradleHomeFlag)
    }
}
