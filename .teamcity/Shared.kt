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
        param("env.GITHUB_BRANCH_NAME", "%teamcity.build.branch%")
        password("env.KORD_TEST_TOKEN", "credentialsJSON:6f4d984c-83d2-4914-8dea-dc0ed996d81d")
        password("env.NEXUS_USER", "credentialsJSON:8e4c071b-df9d-4701-b8df-85058c1bf7ef")
        password("env.NEXUS_PASSWORD", "credentialsJSON:69d63bf7-8064-4062-a01b-96aa50fdc890")
        password("system.org.gradle.project.signingKey", "credentialsJSON:aebe42c4-8827-4b93-afaf-7a1bf4f46a51")
        password("system.org.gradle.project.signingPassword", "credentialsJSON:7de31dac-638d-43b6-a070-9e9d9ed3db22")
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
