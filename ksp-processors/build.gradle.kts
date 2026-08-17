plugins {
    `kord-internal-module`
    org.jetbrains.dokka
    `kord-publishing`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}

publishing {
    publications.register<MavenPublication>(Library.name) {
        from(components["java"])
        artifact(tasks.kotlinSourcesJar)
    }
}
