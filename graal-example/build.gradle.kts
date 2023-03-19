@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kord-internal-module`
    application
    org.graalvm.buildtools.native
}

dependencies {
    implementation(projects.core)
    implementation(libs.slf4j.simple)
    implementation(kotlin("reflect"))
}

application {
    mainClass.set("dev.kord.core.MainKt")
}
