plugins {
    java // for TweetNaclFast
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    api(projects.gateway)

    api(libs.ktor.network)
}
