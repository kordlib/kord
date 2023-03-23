import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubIssues
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.version

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    vcsRoot(HttpsGithubComKordlibKordRefsHeads08x)

    buildType(Build)

    features {
        githubIssues {
            id = "PROJECT_EXT_2"
            displayName = "GitHub"
            repositoryURL = "https://github.com/kordlib/kord"
            authType = accessToken {
                accessToken = "credentialsJSON:8a0ab174-fa52-45d4-950a-05a3c36d0e63"
            }
        }
    }
}

object Build : BuildType({
    name = "Run Native Tests on Linux"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            name = "build"
            tasks = "nativeTest"
            gradleParams = "--stacktrace"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:6c4cf2b5-741b-420a-bd80-587c4746b827"
                }
            }
            param("github_oauth_user", "DRSchlaubi")
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }
})

object HttpsGithubComKordlibKordRefsHeads08x : GitVcsRoot({
    name = "https://github.com/kordlib/kord#refs/heads/0.8.x"
    url = "https://github.com/kordlib/kord"
    branch = "refs/heads/0.8.x"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "DRSchlaubi"
        password = "credentialsJSON:6c4cf2b5-741b-420a-bd80-587c4746b827"
    }
    param("oauthProviderId", "tc-cloud-github-connection")
})
