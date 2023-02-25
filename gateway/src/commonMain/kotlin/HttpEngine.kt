package dev.kord.gateway

import io.ktor.client.engine.*

internal expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
