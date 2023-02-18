import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kord-internal-module`
}

dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    implementation(libs.kotlinx.serialization.json) // use types directly
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }
}
