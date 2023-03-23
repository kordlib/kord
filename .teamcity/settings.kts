import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.version

version = "2022.10"

project {
    vcsRoot(GitHub)

    buildType(ValidationCI)

    features {
        installGitHubIssueTracker()
    }
}
