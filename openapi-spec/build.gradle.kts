plugins {
    `kord-internal-module`
    org.jetbrains.kotlin.plugin.serialization
    application
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

application {
    mainClass = "MainKt"
}
