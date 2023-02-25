package dev.kord.rest.request

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

internal actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by Js
