import jetbrains.buildServer.configs.kotlin.RelativeId

val ValidationCI = KordBuild("Validate Code") {
    id = RelativeId("Validation")
    triggers {
        vcs()
    }

    steps {
        debuggableGradle("Run checks") {
            tasks = "check"
        }
    }
}
