@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kord-internal-module`
    application
    alias(libs.plugins.graal)
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
