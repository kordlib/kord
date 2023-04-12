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

        password("env.KORD_TEST_TOKEN", "credentialsJSON:6f4d984c-83d2-4914-8dea-dc0ed996d81d")
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
