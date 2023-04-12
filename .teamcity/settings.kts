import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.version

version = "2022.10"

project {
    vcsRoot(GitHub)

    buildType(DeploymentCI)
    buildType(DocsCI)
    buildType(QodanaCI)
    // TODO: Await Response from JetBrains to resolve toolchain issues
    //buildType(GraalVMNativeImageCI)

    features {
        installGitHubIssueTracker()
    }
}
