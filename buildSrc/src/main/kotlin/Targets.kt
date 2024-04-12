import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.kpm.external.ExternalVariantApi
import org.jetbrains.kotlin.konan.target.HostManager

@OptIn(ExternalVariantApi::class)
fun KotlinMultiplatformExtension.targets() {
    // There are issues with compiling the linux variant on windows
    // Please use WSL if you need to work on the linux port
    if (!HostManager.hostIsMingw) {
        linuxX64()
        linuxArm64()
    }

    jvm()

    mingwX64()

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
