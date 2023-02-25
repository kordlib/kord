package dev.kord.gateway

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

internal actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by Js

