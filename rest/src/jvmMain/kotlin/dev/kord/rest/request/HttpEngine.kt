package dev.kord.rest.request

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

internal actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
