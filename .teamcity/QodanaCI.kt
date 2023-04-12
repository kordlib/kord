import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.qodana

val QodanaCI = KordBuild("Qodana") {
    id = RelativeId("qodana")

    triggers {
        vcs()
    }

    steps {
        qodana {
            cloudToken = "credentialsJSON:acbac377-214a-4239-a3bb-bcf3ae29d83e"
            additionalQodanaArguments = "--baseline .qodana/baseline.sarif.json"
            linter = Qodana.Linter.Jvm()
        }
    }

    features {
        installGitHubPullRequest()
    }
}
