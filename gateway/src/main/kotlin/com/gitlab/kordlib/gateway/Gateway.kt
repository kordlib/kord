package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * An implementation of the Discord [Gateway](https://discord.com/developers/docs/topics/gateway) and its lifecycle.
 *
 * Allows consumers to receive [events](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
 * through [events] and send [commands](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-commands)
 * through [send].
 */
interface Gateway {
    /**
     * The incoming [events](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
     * of the Gateway. Users should expect these [Flows](Flow) to be hot and remain open for the entire lifecycle of the
     * Gateway.
     */
    val events: Flow<Event>

    /**
     * The duration between the last [Heartbeat] and [HeartbeatACK]. If no [Heartbeat] has been received yet,
     * [Duration.INFINITE] will be returned.
     */
    val ping: Duration

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
     * Close gateway and releases resources.
     *
     * **For some implementations this will render the Gateway unopenable,
     * as such, all implementations should be handled as if they are irreversibly closed.**
     */
    suspend fun detach()

    /**
     * Closes the Gateway and ends the current session, suspending until the underlying webSocket is closed.
     */
    suspend fun stop()

    companion object
}

/**
 * Starts a reconnecting gateway connection with the given parameters.
 * This function will suspend until the lifecycle of the gateway has ended.
 *
 * ```kotlin
 * gateway.start("your_token") {
 *     shard = DiscordShard(0,1)
 *
 *     presence {
 *         afk = false
 *         status = Status.Online
 *         watching("you :eyes:")
 *     }
 *
 * }
 *
 * //gateway has disconnected
 * ```
 *
 * @param token The Discord token of the bot.
 * @param config additional configuration for the gateway.
 */
suspend inline fun Gateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    start(builder.build())
}

/**
 * Enum representation of Discord's [Gateway close event codes](https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-close-event-codes).
 */
enum class GatewayCloseCode(val code: Int) {
    Unknown(4000),
    UnknownOpCode(4001),
    DecodeError(4002),
    NotAuthenticated(4003),
    AuthenticationFailed(4004),
    AlreadyAuthenticated(4005),
    InvalidSeq(4007),
    RateLimited(4008),
    SessionTimeout(4009),
    InvalidShard(4010),
    ShardingRequired(4011),
    InvalidApiVersion(4012),
    InvalidIntents(4013),
    DisallowedIntents(4014)
}