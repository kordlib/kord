import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    api(projects.rest)
    api(projects.gateway)
    api(projects.voice)

    implementation(libs.bundles.common)

    api(libs.kord.cache.api)
    api(libs.kord.cache.map)

    samplesImplementation(libs.slf4j.simple)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + CompilerArguments.stdLib
    }
}
