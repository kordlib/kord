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
        debuggableGradle("Run checks") {
            tasks = "check"
        }

        debuggableGradle("Publish Artifacts") {
            param("env.NEXUS_USER", "credentialsJSON:20058a9f-54e6-4d5a-afbc-a025ab0bf426")
            param("env.NEXUS_PASSWORD", "credentialsJSON:a534b2f5-84e0-4d95-a620-9ef1344d07b3")
//            param("system.org.gradle.project.signingKey", "TODO")
//            param("system.org.gradle.project.signingPassword", "TODO")

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
