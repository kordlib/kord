package dev.kord.gateway

import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalInt
import dev.kord.common.ratelimit.RateLimiter
import dev.kord.gateway.GatewayCloseCode.*
import dev.kord.gateway.handler.*
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.gateway.retry.Retry
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

private val defaultGatewayLogger = KotlinLogging.logger { }

internal expect fun Throwable.isTimeout(): Boolean

private sealed class State(val retry: Boolean) {
    object Stopped : State(false)
    class Running(retry: Boolean) : State(retry)
    object Detached : State(false)
}

/**
 * @param url The url to connect to.
 * @param client The [HttpClient] from which a WebSocket will be created, requires the [WebSockets] plugin to be
 * installed.
 * @param reconnectRetry A [Retry] used for reconnection attempts.
 * @param sendRateLimiter A [RateLimiter] that follows the Discord API specifications for sending messages.
 * @param identifyRateLimiter An [IdentifyRateLimiter] that follows the Discord API specifications for identifying.
 */
public data class DefaultGatewayData(
    val url: String,
    val client: HttpClient,
    val reconnectRetry: Retry,
    val sendRateLimiter: RateLimiter,
    val identifyRateLimiter: IdentifyRateLimiter,
    val dispatcher: CoroutineDispatcher,
    val eventFlow: MutableSharedFlow<Event>,
)

/**
 * The default Gateway implementation of Kord, using an [HttpClient] for the underlying webSocket
 */
public class DefaultGateway(private val data: DefaultGatewayData) : Gateway {

    override val coroutineContext: CoroutineContext = SupervisorJob() + data.dispatcher

    private val compression: Boolean

    private val _ping = MutableStateFlow<Duration?>(null)
    override val ping: StateFlow<Duration?> get() = _ping

    override val events: SharedFlow<Event> = data.eventFlow

    private lateinit var socket: DefaultClientWebSocketSession

    private val state: AtomicRef<State> = atomic(State.Stopped)

    private val handshakeHandler: HandshakeHandler

    private lateinit var inflater: Inflater

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val stateMutex = Mutex()

    init {
        val initialUrl = Url(data.url)
        compression = initialUrl.parameters.contains("compress", "zlib-stream")

        val sequence = Sequence()
        SequenceHandler(events, sequence)
        handshakeHandler = HandshakeHandler(events, initialUrl, ::trySend, sequence, data.reconnectRetry)
        HeartbeatHandler(events, ::trySend, { restart(Close.ZombieConnection) }, { _ping.value = it }, sequence)
        ReconnectHandler(events) { restart(Close.Reconnecting) }
        InvalidSessionHandler(events) { restart(it) }
    }

    //running on default dispatchers because ktor does *not* like running on an EmptyCoroutineContext from main
    override suspend fun start(configuration: GatewayConfiguration): Unit = withContext(Dispatchers.Default) {
        resetState(configuration)

        while (data.reconnectRetry.hasNext && state.value is State.Running) {
            try {
                val (needsIdentify, gatewayUrl) = handshakeHandler.needsIdentifyAndGatewayUrl

                if (needsIdentify) {
                    data.identifyRateLimiter.consume(shardId = configuration.shard.index, events)
                }

                defaultGatewayLogger.trace { "opening gateway connection to $gatewayUrl" }
                socket = data.client.webSocketSession { url(gatewayUrl) }

                /*
                 * https://discord.com/developers/docs/topics/gateway#transport-compression
                 *
                 * > Every connection to the gateway should use its own unique zlib context.
                 */
                inflater = Inflater()
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception) { "" }
                if (exception.isTimeout()) {
                    data.eventFlow.emit(Close.Timeout)
                }

                data.reconnectRetry.retry()
                continue //can't handle a close code if you've got no socket
            }

            try {
                readSocket()
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception) { "" }
            }

            defaultGatewayLogger.trace { "gateway connection closing" }

            try {
                handleClose()
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception) { "" }
            }

            defaultGatewayLogger.trace { "handled gateway connection closed" }

            if (state.value.retry) data.reconnectRetry.retry()
            else data.eventFlow.emit(Close.RetryLimitReached)
        }

        _ping.value = null
        if (!data.reconnectRetry.hasNext) {
            defaultGatewayLogger.warn { "retry limit exceeded, gateway closing" }
        }
    }

    private suspend fun resetState(configuration: GatewayConfiguration) = stateMutex.withLock {
        when (state.value) {
            is State.Running -> throw IllegalStateException(gatewayRunningError)
            State.Detached -> throw IllegalStateException(gatewayDetachedError)
            State.Stopped -> Unit
        }

        handshakeHandler.configuration = configuration
        data.reconnectRetry.reset()
        state.update { State.Running(true) } //resetting state
    }


    private suspend fun readSocket() {
        val frames = socket.incoming.asFlow()
            .buffer(Channel.UNLIMITED)
            .onEach { frame -> defaultGatewayLogger.trace { "Received raw frame: $frame" } }
        val eventsJson = if (compression) {
            frames.decompressFrames(inflater)
        } else {
            frames.mapNotNull { frame ->
                when (frame) {
                    is Frame.Binary, is Frame.Text -> frame.data.decodeToString()
                    else -> null // ignore other frame types
                }
            }
        }
        eventsJson.collect { json ->
            try {
                defaultGatewayLogger.trace { "Gateway <<< $json" }
                val event = jsonParser.decodeFromString(Event.DeserializationStrategy, json)
                data.eventFlow.emit(event)
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception) { "" }
            }
        }
    }

    private suspend fun handleClose() {
        inflater.close()

        val reason = withTimeoutOrNull(1500) {
            socket.closeReason.await()
        } ?: return

        defaultGatewayLogger.trace { "Gateway closed: ${reason.code} ${reason.message}" }
        val discordReason = gatewayCloseCodeByCode[reason.code.toInt()] ?: return

        data.eventFlow.emit(Close.DiscordClose(discordReason, discordReason.retry))

        when {
            !discordReason.retry -> {
                state.update { State.Stopped }
                throw IllegalStateException("Gateway closed: ${reason.code} ${reason.message}")
            }
            discordReason.resetSession -> {
                setStopped()
            }
        }
    }

    // This avoids a bug with the atomicfu compiler plugin
    private fun setStopped() {
        state.update { State.Running(true) }
    }

    private fun <T> ReceiveChannel<T>.asFlow() = flow {
        try {
            for (value in this@asFlow) emit(value)
        } catch (ignore: CancellationException) {
            //reading was stopped from somewhere else, ignore
        }
    }

    override suspend fun stop() {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        data.eventFlow.emit(Close.UserClose)
        state.update { State.Stopped }
        _ping.value = null
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    internal suspend fun restart(code: Close) {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        state.update { State.Running(false) }
        if (socketOpen) {
            data.eventFlow.emit(code)
            socket.close(CloseReason(4900, "reconnecting"))
        }
    }

    override suspend fun detach() {
        (this as CoroutineScope).cancel()
        if (state.value is State.Detached) return
        state.update { State.Detached }
        _ping.value = null
        data.eventFlow.emit(Close.Detach)
        if (::socket.isInitialized) {
            socket.close()
        }
    }

    override suspend fun send(command: Command): Unit = stateMutex.withLock {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        sendUnsafe(command)
    }

    private suspend fun trySend(command: Command) = stateMutex.withLock {
        if (state.value !is State.Running) return@withLock
        sendUnsafe(command)
    }

    private suspend fun sendUnsafe(command: Command) {
        data.sendRateLimiter.consume()
        val json = Json.encodeToString(Command.SerializationStrategy, command)
        if (command is Identify) {
            defaultGatewayLogger.trace {
                val copy = command.copy(token = "token")
                "Gateway >>> ${Json.encodeToString(Command.SerializationStrategy, copy)}"
            }
        } else defaultGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive

    public companion object {
        private const val gatewayRunningError = "The Gateway is already running, call stop() first."
        private const val gatewayDetachedError =
            "The Gateway has been detached and can no longer be used, create a new instance instead."

        private val gatewayCloseCodeByCode = GatewayCloseCode.entries.associateBy { it.code }
    }
}

public inline fun DefaultGateway(builder: DefaultGatewayBuilder.() -> Unit = {}): DefaultGateway {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return DefaultGatewayBuilder().apply(builder).build()
}

internal val GatewayConfiguration.identify
    get() = Identify(
        token,
        IdentifyProperties(os, name, name),
        false.optional(),
        threshold.optionalInt(),
        shard.optional(),
        presence,
        intents
    )


internal expect val os: String

internal val GatewayCloseCode.retry
    get() = when (this) { //this statement is intentionally structured to ensure we consider the retry for every new code
        Unknown -> true
        UnknownOpCode -> true
        DecodeError -> true
        NotAuthenticated -> true
        AuthenticationFailed -> false
        AlreadyAuthenticated -> true
        InvalidSeq -> true
        RateLimited -> true
        SessionTimeout -> true
        InvalidShard -> false
        ShardingRequired -> false
        InvalidApiVersion -> false
        InvalidIntents -> false
        DisallowedIntents -> false
    }

internal val GatewayCloseCode.resetSession
    get() = when (this) { //this statement is intentionally structured to ensure we consider the reset for every new code
        Unknown -> false
        UnknownOpCode -> false
        DecodeError -> false
        NotAuthenticated -> false
        AuthenticationFailed -> false
        AlreadyAuthenticated -> false
        InvalidSeq -> true
        RateLimited -> false
        SessionTimeout -> false
        InvalidShard -> false
        ShardingRequired -> false
        InvalidApiVersion -> false
        InvalidIntents -> false
        DisallowedIntents -> false
    }
