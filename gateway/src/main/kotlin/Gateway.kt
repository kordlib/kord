package dev.kord.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.gateway.builder.PresenceBuilder
import dev.kord.gateway.builder.RequestGuildMembersBuilder
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mu.KLogger
import mu.KotlinLogging
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
public interface Gateway : CoroutineScope {
    /**
     * The incoming [events](https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)
     * of the Gateway. Users should expect these [Flows](Flow) to be hot and remain open for the entire lifecycle of the
     * Gateway.
     */
    public val events: SharedFlow<Event>

    /**
     * The duration between the last [Heartbeat][Command.Heartbeat] and [HeartbeatACK].
     *
     * This flow will have a [value][StateFlow.value] of `null` if the gateway is not
     * [active][Gateway.start], or no [HeartbeatACK] has been received yet.
     */
    public val ping: StateFlow<Duration?>

    /**
     * Starts a reconnecting gateway connection with the given [configuration].
     *
     * After connecting, an [Identify] request will be sent and a session will be created.
     * If you want to resume an already started session, use [resume].
     *
     * This function will suspend until the lifecycle of the gateway has ended.
     *
     * @param configuration the configuration for this gateway session.
     */
    public suspend fun start(configuration: GatewayConfiguration)

    /**
     * Starts a reconnecting gateway connection with the given [configuration].
     *
     * After connecting, a [Resume] request will be sent and the session will be resumed if the session is valid.
     * If the session was invalidated, an identify request will be sent and a session will be created.
     * If you want to start a new session, use [start].
     *
     * This function will suspend until the lifecycle of the gateway has ended.
     *
     * @param configuration the configuration for this gateway session.
     */
    public suspend fun resume(configuration: GatewayResumeConfiguration)

    /**
     * Sends a [Command] to the gateway, suspending until the message has been sent.
     *
     * @param command The [Command] to send to the gateway.
     * @throws Exception when the gateway connection isn't open.
     */
    public suspend fun send(command: Command)

    /**
     * Close gateway and releases resources.
     *
     * **For some implementations this will render the Gateway unopenable,
     * as such, all implementations should be handled as if they are irreversibly closed.**
     */
    public suspend fun detach()

    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    public suspend fun stop() {
        stop(WebSocketCloseReason(1000, "leaving"))
    }

    /**
     * Closes the Gateway, suspending until the underlying WebSocket is closed.
     *
     * By default, the current gateway session will be closed. If you want to keep the session alive to [resume] later,
     * change the [closeReason] to use a different [WebSocketCloseReason.code] that isn't `1000` or `1001`.
     *
     * Returns a [GatewayResumeConfiguration] that can be passed to [resume] later. The
     * [session][GatewayResumeConfiguration.session] will be non-null if there was any successful [Identify]
     * request before closing the connection.
     *
     * @param closeReason the close reason that will be used when closing the WebSocket connection.
     */
    public suspend fun stop(closeReason: WebSocketCloseReason = WebSocketCloseReason(1000, "leaving")): GatewayResumeConfiguration

    public companion object {
        private object None : Gateway {

            override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext

            override val events: SharedFlow<Event>
                get() = MutableSharedFlow()

            override val ping: StateFlow<Duration?>
                get() = MutableStateFlow(null)

            override suspend fun send(command: Command) {}

            override suspend fun start(configuration: GatewayConfiguration) {}

            override suspend fun resume(configuration: GatewayResumeConfiguration) {}

            override suspend fun stop(closeReason: WebSocketCloseReason): GatewayResumeConfiguration { error("Can't stop this!") }

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
        public fun none(): Gateway = None

    }
}

public suspend inline fun Gateway.editPresence(builder: PresenceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val status = PresenceBuilder().apply(builder).toUpdateStatus()
    send(status)
}

/**
 * Closes the Gateway with code 1012, suspending until the underlying webSocket is closed.
 *
 * The session won't be invalidated, and you will be able to [resume] the gateway session. The session will be invalidated by Discord after a few minutes.
 *
 * @return the gateway session information, if there was any successful identify before closing the connection.
 */
public suspend fun Gateway.stopForResume(): GatewayResumeConfiguration = stop(WebSocketCloseReason(1012, "service restart"))

/**
 * Closes the Gateway with code 1000, suspending until the underlying webSocket is closed.
 *
 * The session will be invalidated, and you won't be able to [resume] it. The bot will appear offline in the member list.
 *
 * @return the gateway session information, if there was any successful identify before closing the connection.
 */
public suspend fun Gateway.stopAndInvalidateSession(): GatewayResumeConfiguration = stop(WebSocketCloseReason(1000, "leaving"))


/**
 * Starts a reconnecting gateway connection with the given parameters.
 *
 * After connecting, an [Identify] request will be sent and a session will be created.
 * If you want to resume an already started session, use [resume].
 *
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
public suspend inline fun Gateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(config, InvocationKind.EXACTLY_ONCE)
    }
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    start(builder.build())
}

/**
 * Starts a reconnecting gateway connection with the given parameters.
 *
 * After connecting, a resume request will be sent and the session will be resumed if the session is valid.
 * If the session was invalidated, an identify request will be sent and a session will be created.
 * If you want to start a new session, use [start].
 *
 * This function will suspend until the lifecycle of the gateway has ended.
 *
 * ```kotlin
 * gateway.resume("your_token", "gatewaySessionId", "https://discord-resume-url-here.discord.com/", 0) {
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
 * @param token     The Discord token of the bot.
 * @param session   The gateway session information.
 * @param config additional configuration for the gateway.
 */
public suspend inline fun Gateway.resume(token: String, session: GatewaySession, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(config, InvocationKind.EXACTLY_ONCE)
    }
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    resume(
        GatewayResumeConfiguration(
            session,
            builder.build()
        )
    )
}

/**
 * Logger used to report throwables caught in [Gateway.on].
 */
@PublishedApi
internal val gatewayOnLogger: KLogger = KotlinLogging.logger("Gateway.on")

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
public inline fun <reified T : Event> Gateway.on(
    scope: CoroutineScope = this,
    crossinline consumer: suspend T.() -> Unit
): Job {
    return this.events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        launch { it.runCatching { it.consumer() }.onFailure(gatewayOnLogger::error) }
    }.launchIn(scope)
}

/**
 * Executes a [RequestGuildMembers] command configured by the [builder] for the given [guildId]
 * on this gateway, returning a flow of [GuildMembersChunk] responses.
 *
 * If no [builder] is specified, the request will be configured to fetch all members.
 *
 * The returned flow is cold, and will execute the request only on subscription.
 * Collection of this flow on a [Gateway] that is not [running][Gateway.start]
 * will result in an [IllegalStateException] being thrown.
 *
 * Executing the request on a [Gateway] with a [Shard][dev.kord.common.entity.DiscordShard] that
 * [does not match the guild id](https://discord.com/developers/docs/topics/gateway#sharding)
 * can result in undefined behavior for the returned flow and inconsistencies in the cache.
 *
 * This function expects [request.nonce][RequestGuildMembers.nonce] to contain a value, but it is not required.
 * If no nonce was provided one will be generated instead.
 */
@OptIn(PrivilegedIntent::class)
public fun Gateway.requestGuildMembers(
    guildId: Snowflake,
    builder: RequestGuildMembersBuilder.() -> Unit = { requestAllMembers() }
): Flow<GuildMembersChunk> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = RequestGuildMembersBuilder(guildId).apply(builder).toRequest()
    return requestGuildMembers(request)
}

/**
 * Executes the [request] on this gateway, returning a flow of [GuildMembersChunk] responses.
 *
 * The returned flow is cold, and will execute the [request] only on subscription.
 * Collection of this flow on a [Gateway] that is not [running][Gateway.start]
 * will result in an [IllegalStateException] being thrown.
 *
 * Executing the [request] on a [Gateway] with a [Shard][dev.kord.common.entity.DiscordShard] that
 * [does not match the guild id](https://discord.com/developers/docs/topics/gateway#sharding)
 * can result in undefined behavior for the returned flow and inconsistencies in the cache.
 *
 * This function expects [request.nonce][RequestGuildMembers.nonce] to contain a value, but it is not required.
 * If no nonce was provided one will be generated instead.
 */
@OptIn(PrivilegedIntent::class)
public fun Gateway.requestGuildMembers(request: RequestGuildMembers): Flow<GuildMembersChunk> {
    val nonce = request.nonce.value ?: RequestGuildMembers.Nonce.new()
    val withNonce = request.copy(nonce = Optional.Value(nonce))

    return events
        .onSubscription { send(withNonce) } //send request on subscription
        .filterIsInstance<GuildMembersChunk>()
        .filter { it.data.nonce.value == nonce }
        .transformWhile {
            emit(it)
            return@transformWhile (it.data.chunkIndex + 1) < it.data.chunkCount
        }// 0 <= chunk_index < chunk_count
}

public data class WebSocketCloseReason(val code: Short, val message: String)

/**
 * Enum representation of Discord's [Gateway close event codes](https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-close-event-codes).
 */
public enum class GatewayCloseCode(public val code: Int) {
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
