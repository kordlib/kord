package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.gateway.builder.PresenceBuilder
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * An implementation of the Discord [Gateway](https://discord.com/developers/docs/topics/gateway) and its lifecycle.
 *
 * Allows consumers to receive [events](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
 * through [events] and send [commands](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-commands)
 * through [send].
 */
interface Gateway : CoroutineScope {
    /**
     * The incoming [events](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
     * of the Gateway. Users should expect these [Flows](Flow) to be hot and remain open for the entire lifecycle of the
     * Gateway.
     */
    val events: SharedFlow<Event>

    /**
     * The duration between the last [Heartbeat] and [HeartbeatACK].
     *
     * This flow will have a [value][StateFlow.value] of `null` if the gateway is not
     * [active][Gateway.start], or no [HeartbeatACK] has been received yet.
     */
    val ping: StateFlow<Duration?>

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

    companion object {
        private object None : Gateway {

            override val coroutineContext: CoroutineContext = EmptyCoroutineContext + SupervisorJob()

            override val events: SharedFlow<Event>
                get() = MutableSharedFlow()

            override val ping: StateFlow<Duration?>
                get() = MutableStateFlow(null)

            override suspend fun send(command: Command) {}

            override suspend fun start(configuration: GatewayConfiguration) {}

            override suspend fun stop() {}

            override suspend fun detach() {
                (this as CoroutineScope).cancel()
            }

            override fun toString(): String {
                return "Gateway.None"
            }
        }

        /**
         * Returns a [Gateway] with no-op behavior, an empty [Gateway.events] flow and a ping of [Duration.ZERO].
         */
        fun none(): Gateway = None

    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun Gateway.editPresence(builder: PresenceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val status = PresenceBuilder().apply(builder).toUpdateStatus()
    send(status)
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
@OptIn(ExperimentalContracts::class)
suspend inline fun Gateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(config, InvocationKind.EXACTLY_ONCE)
    }
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    start(builder.build())
}

/**
 * Logger used to report throwables caught in [Gateway.on].
 */
@PublishedApi
internal val gatewayOnLogger = KotlinLogging.logger("Gateway.on")

/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [Gateway.events].
 *
 * The events are buffered in an [unlimited][Channel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [Gateway] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events for this [consumer].
 */
inline fun <reified T : Event> Gateway.on(scope: CoroutineScope = this, crossinline consumer: T.() -> Unit): Job {
    return this.events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        launch { it.runCatching { it.consumer() }.onFailure(gatewayOnLogger::error) }
    }.launchIn(scope)
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