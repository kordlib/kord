plugins {
    `kord-module`
    `kord-sampled-module`
}

dependencies {
    api(common)
    implementation(libs.bundles.common)

    api(libs.ktor.client.json)
    api(libs.ktor.client.websockets)
    api(libs.ktor.client.serialization)
    api(libs.ktor.client.cio)


    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}
