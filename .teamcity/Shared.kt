import jetbrains.buildServer.configs.kotlin.*

@Suppress("FunctionName")
fun KordBuild(name: String, configure: BuildType.() -> Unit) = object : BuildType({
    this.name = name
    configure()

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        checkbox(debugParamName, false.toString(), "Debug Mode", "Run build with debug logging enabled")

        param("env.GITHUB_BRANCH", "%teamcity.build.branch%")

        password("env.KORD_TEST_TOKEN", "credentialsJSON:cbd8be1d-4808-42ff-90cf-551e9f60e83d")
    }

    features {
        installGitHubPublisher()
    }
}) {}
