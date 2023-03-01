package dev.kord.common.http

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
