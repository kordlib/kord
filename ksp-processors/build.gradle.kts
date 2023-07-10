plugins {
    `kord-internal-module`
    `kord-publishing`
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}
