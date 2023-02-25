plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)

    api(libs.bundles.ktor.client.serialization)
    api(libs.ktor.client.websockets)
    api(libs.ktor.client.cio)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}
