plugins {
    `kord-module`
    `kord-sampled-module`
}

dependencies {
    api(libs.kotlinx.datetime)

    implementation(libs.bundles.common)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}
