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
    }

    failureConditions {
        failOnSignificantDecreaseOf(BuildFailureOnMetric.MetricType.TEST_COUNT)
        failOnSignificantDecreaseOf(BuildFailureOnMetric.MetricType.TEST_IGNORED_COUNT)
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
