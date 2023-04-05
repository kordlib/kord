package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.*

/** @suppress */
@KordInternal
public expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
