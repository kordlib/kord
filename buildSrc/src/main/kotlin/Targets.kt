import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager

fun KotlinMultiplatformExtension.targets() {
    jvm()

    js {
        nodejs {
            testTask {
                useMocha {
                    // disable timeouts, some tests are too slow for default 2-second timeout:
                    // https://mochajs.org/#-timeout-ms-t-ms
                    timeout = "0"
                }
            }
        }
        useCommonJs()
    }

    linuxX64()
    linuxArm64()

    mingwX64()

    macosArm64()
    macosX64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosArm64()
    watchosSimulatorArm64()

    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    targets.all {
        compilations.all {
            compilerOptions.options.applyKordCompilerOptions()
        }
    }
}

// There are issues with linking the linux variant on windows.
// Please use WSL if you need to work on the linux port.
fun TaskContainer.disableLinuxLinkTestTasksOnWindows() {
    if (HostManager.hostIsMingw) {
        val linuxLinkTestTasks = listOf("linkDebugTestLinuxX64", "linkDebugTestLinuxArm64")
        for (task in linuxLinkTestTasks) {
            named(task) {
                enabled = false
            }
        }
    }
}
