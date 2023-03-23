import jetbrains.buildServer.configs.kotlin.BuildSteps
import jetbrains.buildServer.configs.kotlin.buildSteps.GradleBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle

const val debugParamName = "debug"

/**
 * Adds a new [Gradle build step][GradleBuildStep] that supports the debug parameter.
 */
fun BuildSteps.debuggableGradle(name: String, configure: GradleBuildStep.() -> Unit) {
    gradle {
        this.name = "$name (Debug)"
        id = name
        conditions {
            equals(debugParamName, true.toString())
        }
        configure()
        gradleParams = (gradleParams?.plus(' ') ?: "") + "-d"
    }

    gradle {
        this.name = name
        id="${name}_debug"
        conditions {
            equals(debugParamName, false.toString())
        }
        configure()
    }
}
