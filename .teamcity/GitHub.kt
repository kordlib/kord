import jetbrains.buildServer.configs.kotlin.BuildFeatures
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.ProjectFeatures
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubIssues
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

object GitHub : GitVcsRoot({
    name = "https://github.com/kordlib/kord#refs/heads/0.8.x"
    url = "https://github.com/kordlib/kord"
    branch = "refs/heads/0.8.x"
    branchSpec = """
        +:refs/heads/*
        -:refs/heads/gh-pages
    """.trimIndent()
    authMethod = password {
        userName = "DRSchlaubi"
        password = "credentialsJSON:6c4cf2b5-741b-420a-bd80-587c4746b827"
    }
    param("oauthProviderId", "tc-cloud-github-connection")
})

fun BuildFeatures.installGitHubPublisher() = commitStatusPublisher {
    vcsRootExtId = "${DslContext.settingsRoot.id}"
    publisher = github {
        githubUrl = "https://api.github.com"
        authType = personalToken {
            token = "credentialsJSON:6c4cf2b5-741b-420a-bd80-587c4746b827"
        }
    }
    param("github_oauth_user", "DRSchlaubi")
}

fun BuildFeatures.installGitHubPullRequest() = pullRequests {
    provider = github {
        authType = token {
            token = "credentialsJSON:8a0ab174-fa52-45d4-950a-05a3c36d0e63"
        }
    }
}

fun ProjectFeatures.installGitHubIssueTracker() = githubIssues {
    id = "PROJECT_EXT_2"
    displayName = "GitHub"
    repositoryURL = "https://github.com/kordlib/kord"
    authType = accessToken {
        accessToken = "credentialsJSON:8a0ab174-fa52-45d4-950a-05a3c36d0e63"
    }
}
