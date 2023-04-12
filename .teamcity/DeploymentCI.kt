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
            param("env.NEXUS_USER", "credentialsJSON:1fcb58be-049c-43cc-b002-582f691f12c0")
            param("env.NEXUS_PASSWORD", "credentialsJSON:f7652101-08d1-4abf-842a-4926fdaa749c")
            param("system.org.gradle.project.signingKey", "credentialsJSON:aebe42c4-8827-4b93-afaf-7a1bf4f46a51")
            param("system.org.gradle.project.signingPassword", "credentialsJSON:7de31dac-638d-43b6-a070-9e9d9ed3db22")

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
