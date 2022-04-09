plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    api(projects.gateway)

    api(libs.ktor.client.json)
    api(libs.bundles.ktor.client.serialization)
    api(libs.ktor.client.cio)
    api(libs.ktor.network)
}

// by convention, java classes (TweetNaclFast) should be in their own java source.
// however, this breaks atomicfu.
// to work around it we just make the kotlin src directory also a java src directory.
// this can be removed when https://github.com/Kotlin/kotlinx.atomicfu/commit/fe0950adcf0da67cd074503c2a78467656c5aa0f is released.
sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}
