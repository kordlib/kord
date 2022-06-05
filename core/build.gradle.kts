import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

val voice by sourceSets.creating

configurations {
    getByName("voiceImplementation") {
        extendsFrom(implementation.get())
    }
}

dependencies {
    api(projects.common)
    api(projects.rest)
    api(projects.gateway)
    "voiceApi"(projects.core)
    "voiceApi"(projects.voice)

    implementation(libs.bundles.common)

    api(libs.kord.cache.api)
    api(libs.kord.cache.map)

    samplesImplementation(libs.slf4j.simple)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + CompilerArguments.stdLib
        }
    }
}

java {
    registerFeature("voice") {
        usingSourceSet(voice)
        withJavadocJar()
        withSourcesJar()
        capability("dev.kord", "core-voice", version as String)
    }
}
