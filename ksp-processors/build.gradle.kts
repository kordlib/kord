plugins {
    `kord-internal-module`
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.codegen.kt.dsl)
    implementation(libs.codegen.kt.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}
