package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.ratelimit.RateLimiter
import com.gitlab.kordlib.gateway.GatewayCloseCode.*
import com.gitlab.kordlib.gateway.handler.*
import com.gitlab.kordlib.gateway.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.URLBuilder
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.util.error
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream
import kotlin.time.Duration

private val defaultGatewayLogger = KotlinLogging.logger { }

private sealed class State(val retry: Boolean) {
    object Stopped : State(false)
    class Running(retry: Boolean) : State(retry)
    object Detached : State(false)
}

/**
 * @param url The url to connect to.
 * @param client The client from which a webSocket will be created, requires the WebSockets and JsonFeature to be
 * installed.
 * @param reconnectRetry A retry used for reconnection attempts.
 * @param sendRateLimiter A rate limiter than follows the Discord API specifications for sending messages.
 * @param identifyRateLimiter: A rate limiter that follows the Discord API specifications for identifying.
 */
data class DefaultGatewayData(
        val url: String,
        val client: HttpClient,
        val reconnectRetry: Retry,
        val sendRateLimiter: RateLimiter,
        val identifyRateLimiter: RateLimiter
)

/**
 * The default Gateway implementation of Kord, using an [HttpClient] for the underlying webSocket
 */
@ObsoleteCoroutinesApi
class DefaultGateway(private val data: DefaultGatewayData) : Gateway {

    private val compression: Boolean = URLBuilder(data.url).parameters.contains("compress", "zlib-stream")

    private val channel = BroadcastChannel<Any>(Channel.CONFLATED)

    override var ping: Duration = Duration.INFINITE

    @OptIn(FlowPreview::class)
    override val events: Flow<Event> = channel.asFlow().drop(1).filterIsInstance()

    private lateinit var socket: DefaultClientWebSocketSession

    private val state: AtomicRef<State> = atomic(State.Stopped)

    private val handshakeHandler: HandshakeHandler

    private lateinit var inflater: Inflater

    private val jsonParser = Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
    }

    private val stateMutex = Mutex()

    init {
        channel.sendBlocking(Unit)
        val sequence = Sequence()
        SequenceHandler(events, sequence)
        handshakeHandler = HandshakeHandler(events, ::trySend, sequence, data.identifyRateLimiter)
        HeartbeatHandler(events, ::trySend, { restart(Close.ZombieConnection) }, { ping = it }, sequence)
        ReconnectHandler(events) { restart(Close.Reconnecting) }
        InvalidSessionHandler(events) { restart(it) }
    }

    //running on default dispatchers because ktor does *not* like running on an EmptyCoroutineContext from main
    override suspend fun start(configuration: GatewayConfiguration): Unit = withContext(Dispatchers.Default) {
        resetState(configuration)

        while (data.reconnectRetry.hasNext && state.value is State.Running) {
            try {
                socket = webSocket(data.url)
                /**
                 * https://discord.com/developers/docs/topics/gateway#transport-compression
                 *
                 * > Every connection to the gateway should use its own unique zlib context.
                 */
                inflater = Inflater()
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
                if (exception is java.nio.channels.UnresolvedAddressException) {
                    channel.send(Close.Timeout)
                }

                data.reconnectRetry.retry()
                continue //can't handle a close code if you've got no socket
            }

            try {
                readSocket()
                data.reconnectRetry.reset() //connected and read without problems, resetting retry counter
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
            }

            defaultGatewayLogger.trace { "gateway connection closing" }

            try {
                handleClose()
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
            }

            defaultGatewayLogger.trace { "handled gateway connection closed" }

            if (state.value.retry) data.reconnectRetry.retry()
            else channel.send(Close.RetryLimitReached)
        }

        if (!data.reconnectRetry.hasNext) {
            defaultGatewayLogger.warn { "retry limit exceeded, gateway closing" }
        }
    }

    private suspend fun resetState(configuration: GatewayConfiguration) = stateMutex.withLock {
        @Suppress("UNUSED_VARIABLE")
        val exhaustive = when (state.value) { //exhaustive state checking
            is State.Running -> throw IllegalStateException(gatewayRunningError)
            State.Detached -> throw IllegalStateException(gatewayDetachedError)
            State.Stopped -> Unit
        }

        handshakeHandler.configuration = configuration
        data.reconnectRetry.reset()
        state.update { State.Running(true) } //resetting state
    }


    private suspend fun readSocket() {
        socket.incoming.asFlow().collect {
            when (it) {
                is Frame.Binary, is Frame.Text -> read(it)
                else -> { /*ignore*/
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
            outputStream.toString(Charsets.UTF_8)
        }
    }

    private suspend fun read(frame: Frame) {
        val json = when {
            compression -> frame.deflateData()
            else -> String(frame.data, Charsets.UTF_8)
        }

        try {
            defaultGatewayLogger.trace { "Gateway <<< $json" }
            jsonParser.decodeFromString(Event.Companion, json)?.let { channel.send(it) }
        } catch (exception: Exception) {
            defaultGatewayLogger.error(exception)
        }

    }

    private suspend fun handleClose() {
        val reason = withTimeoutOrNull(1500) {
            socket.closeReason.await()
        } ?: return

        defaultGatewayLogger.trace { "Gateway closed: ${reason.code} ${reason.message}" }
        val discordReason = values().firstOrNull { it.code == reason.code.toInt() } ?: return

        channel.send(Close.DiscordClose(discordReason, discordReason.retry))

        when {
            !discordReason.retry -> {
                state.update { State.Stopped }
                throw  IllegalStateException("Gateway closed: ${reason.code} ${reason.message}")
            }
            discordReason.resetSession -> {
                state.update { State.Running(true) }
            }
        }
    }

    private fun <T> ReceiveChannel<T>.asFlow() = flow {
        try {
            for (value in this@asFlow) emit(value)
        } catch (ignore: CancellationException) {
            //reading was stopped from somewhere else, ignore
        }
    }

    private suspend fun webSocket(url: String) = data.client.webSocketSession { url(url) }

    override suspend fun stop() {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        channel.send(Close.UserClose)
        state.update { State.Stopped }
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    internal suspend fun restart(code: Close) {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        state.update { State.Running(false) }
        if (socketOpen) {
            channel.send(code)
            socket.close(CloseReason(4900, "reconnecting"))
        }
    }

    override suspend fun detach() {
        if (state.value is State.Detached) return
        state.update { State.Detached }
        channel.send(Close.Detach)
        if (::socket.isInitialized) {
            socket.close()
        }
        channel.cancel()
    }

    override suspend fun send(command: Command) = stateMutex.withLock {
        check(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        sendUnsafe(command)
    }

    private suspend fun trySend(command: Command) = stateMutex.withLock {
        if (state.value !is State.Running) return@withLock
        sendUnsafe(command)
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private suspend fun sendUnsafe(command: Command) {
        data.sendRateLimiter.consume()
        val json = Json.encodeToString(Command.Companion, command)
        if (command is Identify) {
            defaultGatewayLogger.trace {
                val copy = command.copy(token = "token")
                "Gateway >>> ${Json.encodeToString(Command.Companion, copy)}"
            }
        }
        else defaultGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive

    companion object {

        inline operator fun invoke(builder: DefaultGatewayBuilder.() -> Unit = {}): DefaultGateway =
                DefaultGatewayBuilder().apply(builder).build()

        private const val gatewayRunningError = "The Gateway is already running, call stop() first."
        private const val gatewayDetachedError = "The Gateway has been detached and can no longer be used, create a new instance instead."
    }
}

internal val GatewayConfiguration.identify get() = Identify(token, IdentifyProperties(os, name, name), false, 50, shard, presence, intents)

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
