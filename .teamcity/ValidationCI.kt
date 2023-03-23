val ValidationCI = KordBuild("Validate Code") {
    triggers {
        vcs()
    }

    steps {
        debuggableGradle("Run checks") {
            tasks = "check"
        }
    }
}
