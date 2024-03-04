import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    org.jetbrains.kotlin.multiplatform
}

kotlin {
    // There are issues with compiling the linux variant on windows
    // Please use WSL if you need to work on the linux port
    if (!HostManager.hostIsMingw) {
        linuxX64()
        linuxArm64()
    }

    jvm()

    if (name != "voice" && name != "core-voice") {
        mingwX64()
        js {
            nodejs()
            useCommonJs()
        }
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
}
