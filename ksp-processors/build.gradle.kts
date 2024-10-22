plugins {
    `kord-internal-module`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.codegen.kt.kotlinpoet)
    implementation(libs.codegen.kt.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}
