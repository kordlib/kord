plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(common)
    api(gateway)

    implementation(libs.codahale.xsalsa20poly1305)
    api(libs.ktor.client.json)
    api(libs.ktor.client.serialization)
    api(libs.ktor.client.cio)
    api(libs.ktor.network)
}
