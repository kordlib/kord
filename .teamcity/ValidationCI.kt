import jetbrains.buildServer.configs.kotlin.RelativeId

val ValidationCI = KordBuild("Validate Code") {
    id = RelativeId("Kord_Validation")
    triggers {
        vcs()
    }

    steps {
        debuggableGradle("Run checks") {
            tasks = "check"
        }
    }
}
