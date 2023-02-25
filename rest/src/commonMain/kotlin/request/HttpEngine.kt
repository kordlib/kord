package dev.kord.rest.request

import io.ktor.client.engine.*

internal expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
