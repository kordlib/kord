plugins {
    `kord-internal-module`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-receivers",
            "-Xsuppress-warning=CONTEXT_RECEIVERS_DEPRECATED",
        )
    }
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}
