plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    implementation(libs.bundles.common)

    api(libs.ktor.client.cio)
    api(libs.ktor.client.json)
    api(libs.bundles.ktor.client.serialization)

    testImplementation(libs.ktor.client.mock)

    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}
