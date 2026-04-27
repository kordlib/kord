plugins {
    java // for TweetNaclFast
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

repositories {
    maven("https://maven.lavalink.dev/releases")
}

dependencies {
    api(projects.common)
    api(projects.gateway)

    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.api)

    compileOnly(projects.kspAnnotations)

    api(libs.ktor.network)

    implementation(libs.libsodiumBindings)
    compileOnly(libs.jna)

    // libdave-jvm for DAVE protocol — versions defined in libs.versions.toml
    implementation(libs.libdave.api)
    implementation(libs.libdave.impl.jni)

    // Native libraries for libdave — include all platforms, loader picks the right one at runtime
    runtimeOnly(libs.libdave.natives.darwin)
    runtimeOnly(libs.libdave.natives.linuxX64)
    runtimeOnly(libs.libdave.natives.linuxAarch64)
    runtimeOnly(libs.libdave.natives.winX64)

    testImplementation(libs.bundles.test.jvm)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.bundles.test.jvm.runtime)
}
