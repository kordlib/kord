package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.*
import io.ktor.client.engine.winhttp.*

/** @suppress */
@KordInternal
public actual fun httpEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = WinHttp
