import jetbrains.buildServer.configs.kotlin.FailureConditions
import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange

val DeploymentCI = KordBuild("Deployment") {
    id = RelativeId("deployment")
    triggers {
        vcs()
    }

    steps {
        debuggableGradle("Run checks") {
            tasks = "check"
        }

        debuggableGradle("Publish Artifacts") {
            conditions {
                // Meaning: Do not run on Pull Requests
                doesNotExist("teamcity.pullRequest.number")
            }

            tasks = "publish"
            gradleParams = "-x test"
        }
    }

    failureConditions {
        failOnSignificantDecreaseOf(BuildFailureOnMetric.MetricType.TEST_COUNT)
        failOnSignificantDecreaseOf(BuildFailureOnMetric.MetricType.TEST_IGNORED_COUNT)
    }

    features {
        installGitHubPullRequest()
    }
}

private fun FailureConditions.failOnSignificantDecreaseOf(metricType: BuildFailureOnMetric.MetricType) {
    failOnMetricChange {
        metric = metricType
        threshold = 20
        units = BuildFailureOnMetric.MetricUnit.PERCENTS
        comparison = BuildFailureOnMetric.MetricComparison.LESS
        compareTo = build {
            buildRule = lastSuccessful()
        }
    }
}
