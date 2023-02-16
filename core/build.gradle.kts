plugins {
    java
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

val voice: SourceSet by sourceSets.creating
val voiceApi: Configuration by configurations.getting

configurations {
    getByName("voiceImplementation") {
        extendsFrom(implementation.get())
    }
}

dependencies {
    api(projects.common)
    api(projects.rest)
    api(projects.gateway)
    voiceApi(projects.core)
    voiceApi(projects.voice)

    api(libs.kord.cache.api)
    api(libs.kord.cache.map)

    samplesImplementation(libs.slf4j.simple)

    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

java {
    registerFeature("voice") {
        usingSourceSet(voice)
        withJavadocJar()
        withSourcesJar()
        capability("dev.kord", "core-voice", version as String)
    }
}
