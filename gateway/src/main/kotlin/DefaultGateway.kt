package dev.kord.gateway

import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalInt
import dev.kord.common.ratelimit.RateLimiter
import dev.kord.gateway.GatewayCloseCode.*
import dev.kord.gateway.handler.*
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

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
public class DefaultGateway(private val data: DefaultGatewayData) : BaseGateway() {

    private val compression: Boolean
    private val _ping = MutableStateFlow<Duration?>(null)

    override val ping: StateFlow<Duration?> get() = _ping
    override val events: MutableSharedFlow<Event> = data.eventFlow

    private lateinit var socket: DefaultClientWebSocketSession

    private val handshakeHandler: HandshakeHandler

    private lateinit var inflater: Inflater

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override val dispatcher: CoroutineDispatcher = data.dispatcher

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

    override suspend fun onStart(configuration: GatewayConfiguration): Unit = withContext(Dispatchers.Default) {
        handshakeHandler.configuration = configuration
        data.reconnectRetry.reset()

        while (data.reconnectRetry.hasNext && state is State.Running) {
            try {
                val (needsIdentify, gatewayUrl) = handshakeHandler.needsIdentifyAndGatewayUrl

                if (needsIdentify) {
                    data.identifyRateLimiter.consume(shardId = configuration.shard.index, events)
                }

                log.trace { "Opening gateway connection to $gatewayUrl." }
                socket = data.client.webSocketSession { url(gatewayUrl) }

                /**
                 * https://discord.com/developers/docs/topics/gateway#transport-compression
                 *
                 * > Every connection to the gateway should use its own unique zlib context.
                 */
                inflater = Inflater()
            } catch (exception: Exception) {
                log.error(exception)
                if (exception is java.nio.channels.UnresolvedAddressException) {
                    data.eventFlow.emit(Close.Timeout)
                }

                data.reconnectRetry.retry()
                continue //can't handle a close code if you've got no socket
            }

            try {
                readSocket()
            } catch (exception: Exception) {
                log.error(exception)
            }

            log.trace { "Gateway connection closing." }

            try {
                handleClose()
            } catch (exception: Exception) {
                log.error(exception)
            }

            log.trace { "Handled gateway connection closed." }

            if (state.retry) {
                data.reconnectRetry.retry()
            } else {
                events.emit(Close.RetryLimitReached)
            }
        }

        _ping.value = null
        if (!data.reconnectRetry.hasNext) {
            log.warn { "Retry limit exceeded, gateway closing." }
        }
    }

    private suspend fun readSocket() {
        socket.incoming.asFlow().buffer(Channel.UNLIMITED).collect {
            when (it) {
                is Frame.Binary, is Frame.Text -> read(it)
                else -> { /* Ignored. */
                }
            }
        }
    }

    private fun Frame.deflateData(): String {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, inflater).use {
            it.write(data)
        }

        return outputStream.use {
            String(outputStream.toByteArray(), 0, outputStream.size(), Charsets.UTF_8)
        }
    }

    private suspend fun read(frame: Frame) {
        val json = when {
            compression -> frame.deflateData()
            else -> String(frame.data, Charsets.UTF_8)
        }

        try {
            log.trace { "Gateway <<< $json" }
            val event = jsonParser.decodeFromString(Event.DeserializationStrategy, json) ?: return
            events.emit(event)
        } catch (exception: Exception) {
            log.error(exception)
        }

    }

    private suspend fun handleClose() {
        val reason = withTimeoutOrNull(1500) {
            socket.closeReason.await()
        } ?: return

        log.trace { "Gateway closed: ${reason.code} ${reason.message}" }
        val discordReason = values().firstOrNull { it.code == reason.code.toInt() } ?: return

        data.eventFlow.emit(Close.DiscordClose(discordReason, discordReason.retry))

        when {
            !discordReason.retry -> {
                state = State.Stopped
                throw IllegalStateException("Gateway closed: ${reason.code} ${reason.message}")
            }

            discordReason.resetSession -> {
                state = State.Running(true)
            }
        }
    }

    private fun <T> ReceiveChannel<T>.asFlow() = flow {
        try {
            for (value in this@asFlow) emit(value)
        } catch (ignore: CancellationException) {
            // Reading was stopped from somewhere else, ignored.
        }
    }

    override suspend fun onStop() {
        _ping.value = null
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    internal suspend fun restart(code: Close) {
        requireStateIsNot<State.Detached>()
        state = State.Running(false)
        if (socketOpen) {
            data.eventFlow.emit(code)
            socket.close(CloseReason(4900, "reconnecting"))
        }
    }

    override suspend fun onDetach() {
        _ping.value = null
        if (::socket.isInitialized) {
            socket.close()
        }
    }

    override suspend fun onSend(command: Command) {
        sendUnsafe(command)
    }

    private suspend fun trySend(command: Command) = stateMutex.withLock {
        if (state !is State.Running) return@withLock
        sendUnsafe(command)
    }

    private suspend fun sendUnsafe(command: Command) {
        data.sendRateLimiter.consume()
        val json = Json.encodeToString(Command.SerializationStrategy, command)
        if (command is Identify) {
            log.trace {
                val copy = command.copy(token = "Hidden")
                "Gateway >>> ${Json.encodeToString(Command.SerializationStrategy, copy)}"
            }
        } else log.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive

    public companion object
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


internal val os: String get() = System.getProperty("os.name")

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
