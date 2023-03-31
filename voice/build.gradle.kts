plugins {
    java // for TweetNaclFast
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    api(projects.gateway)

    compileOnly(projects.kspAnnotations)

    api(libs.ktor.network)
}
