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
        password("env.NEXUS_USER", "credentialsJSON:1fcb58be-049c-43cc-b002-582f691f12c0")
        password("env.NEXUS_PASSWORD", "credentialsJSON:f7652101-08d1-4abf-842a-4926fdaa749c")
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
