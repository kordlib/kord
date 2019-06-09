package com.gitlab.hopebaron.websocket

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

interface Gateway {
    @FlowPreview
    val events: Flow<Event>

    suspend fun start(configuration: GatewayConfiguration)
    suspend fun send(command: Command)
    suspend fun close()
}

suspend inline fun Gateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    start(builder.build())
}
