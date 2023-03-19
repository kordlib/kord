plugins {
    `kord-internal-module`
    application
    id("org.graalvm.buildtools.native") version "0.9.20"
}

dependencies {
    implementation(projects.core)
    implementation(libs.slf4j.simple)
}

application {
    mainClass.set("dev.kord.core.MainKt")
}

graalvmNative {
    binaries {
        named("main") {
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(19))
                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
            })
        }
    }
}
