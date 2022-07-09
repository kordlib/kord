plugins {
    `kord-internal-module`
}

dependencies {
    implementation(projects.kspAnnotations)
    implementation(libs.bundles.ksp.processors)
    implementation(libs.bundles.common)
}
