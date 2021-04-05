package dev.kord.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.gateway.builder.PresenceBuilder
import dev.kord.gateway.builder.RequestGuildMembersBuilder
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerialName
import mu.KotlinLogging
import java.util.*
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
inline fun <reified T : Event> Gateway.on(scope: CoroutineScope = this, crossinline consumer: suspend T.() -> Unit): Job {
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
@OptIn(PrivilegedIntent::class, ExperimentalContracts::class)
fun Gateway.requestGuildMembers(
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
fun Gateway.requestGuildMembers(request: RequestGuildMembers): Flow<GuildMembersChunk> {
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
