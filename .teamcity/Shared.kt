import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.buildCache

@Suppress("FunctionName")
fun KordBuild(name: String, configure: BuildType.() -> Unit) = object : BuildType({
    this.name = name
    configure()

    vcs {
        root(DslContext.settingsRoot)
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }

    params {
        checkbox(debugParamName, false.toString(), "Debug Mode", "Run build with debug logging enabled")

        param("env.GITHUB_BRANCH", "%teamcity.build.branch%")

        password("env.KORD_TEST_TOKEN", "credentialsJSON:c4175048-ffe1-4312-97ec-6a0e2eb5647d")
    }

    features {
        installGitHubPublisher()
        buildCache {
            this.name = "gradle_cache"
            rules = """
                .gradle/
                buildSrc/.gradle
                buildSrc/build
                ~/.gradle/caches
            """.trimIndent()
        }
    }
}) {}
