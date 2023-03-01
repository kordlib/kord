package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.*

@KordInternal
public expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
