import jetbrains.buildServer.configs.kotlin.FailureConditions
import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange

val ValidationCI = KordBuild("Validate Code") {
    id = RelativeId("Validation")
    triggers {
        vcs()
    }

    steps {
        password("env.KORD_TEST_TOKEN", "credentialsJSON:cbd8be1d-4808-42ff-90cf-551e9f60e83d")
        debuggableGradle("Run checks") {
            tasks = "check"
        }

        debuggableGradle("Publish Artifacts") {
            // Secrets need to be specified
            enabled = false
            param("env.NEXUS_USER", "TODO")
            param("env.NEXUS_PASSWORD", "TODO")
            param("system.org.gradle.project.signingKey", "TODO")
            param("system.org.gradle.project.signingPassword", "TODO")

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
