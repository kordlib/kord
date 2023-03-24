import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.triggers.vcs

val DocsCI = KordBuild("Deploy documentation") {
    id = RelativeId("Deploy_docs")

    triggers {
        vcs {
            branchFilter = "+:0.8.x"
        }
    }

    steps {
        debuggableGradle("Deploy documentation") {
            tasks = "gitPublishPush"
        }
    }
}
