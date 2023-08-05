package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*

/** @suppress */
@KordInternal
public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by Curl
