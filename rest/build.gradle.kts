plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)

    api(libs.bundles.ktor.client.serialization)
    api(libs.ktor.client.cio)

    ksp(projects.kspProcessors)

    testImplementation(libs.bundles.test.implementation)
    testImplementation(libs.ktor.client.mock)
    testRuntimeOnly(libs.bundles.test.runtime)
}
