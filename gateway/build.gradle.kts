plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    implementation(libs.bundles.common)

    api(libs.ktor.client.json)
    api(libs.ktor.client.websockets)
    api(libs.bundles.ktor.client.serialization)
    api(libs.ktor.client.cio)


    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}
