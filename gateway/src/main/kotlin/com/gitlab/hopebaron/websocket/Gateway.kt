package com.gitlab.hopebaron.websocket

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * An implementation of the Discord [Gateway](https://discordapp.com/developers/docs/topics/gateway) and its lifecycle.
 *
 * Allows consumers to receive [events](https://discordapp.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
 * through [events] and send [commands](https://discordapp.com/developers/docs/topics/gateway#commands-and-events-gateway-commands)
 * through [send].
 */
interface Gateway {
    /**
     * The incoming [events](https://discordapp.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
     * of the Gateway. Users should expect these [Flows](Flow) to be hot and remain open for the entire lifecycle of the
     * Gateway.
     */
    @FlowPreview
    val events: Flow<Event>

    /**
     * Starts a reconnection gateway connection with the given [configuration].
     * This function will suspend until the lifecycle of the gateway has ended.
     *
     * @param configuration the configuration for this gateway session.
     */
    suspend fun start(configuration: GatewayConfiguration)

    /**
     * Sends a [Command] to the gateway, suspending until the message has been sent.
     *
     * @param command The [Command] to send to the gateway.
     * @throws Exception when the gateway connection isn't open.
     */
    suspend fun send(command: Command)

    /**
     * Closes the Gateway and ends the current session, suspending until the underlying webSocket is closed.
     */
    suspend fun close()
}

/**
 * Starts a reconnecting gateway connection with the given parameters.
 * This function will suspend until the lifecycle of the gateway has ended.
 *
 * @param token the configuration for this gateway session.
 * @param config additional configuration for the gateway, using sensible defaults
 */
suspend inline fun Gateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    start(builder.build())
}
