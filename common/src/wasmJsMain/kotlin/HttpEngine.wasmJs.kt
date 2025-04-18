package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

@KordInternal
public actual fun httpEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = Js