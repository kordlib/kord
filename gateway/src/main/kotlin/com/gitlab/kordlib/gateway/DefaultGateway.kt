package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.ratelimit.RateLimiter
import com.gitlab.kordlib.gateway.handler.*
import com.gitlab.kordlib.gateway.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.util.error
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.time.Duration

private val defaultGatewayLogger = KotlinLogging.logger { }

private sealed class State(val retry: Boolean) {
    object ShutDown : State(false)
    class Restart(retry: Boolean) : State(retry)
    object Detached : State(false)
}

/**
 * The default Gateway implementation of Kord, using an [HttpClient] for the underlying webSocket
 *
 * @param url The url to connect to.
 * @param client The client from which a webSocket will be created, requires the [WebSockets] and [JsonFeature] to be
 * installed.
 * @param rateLimiter A rate limiter than follows the Discord API specifications.
 * @param retry A retry used for reconnection attempts.
 */
@FlowPreview
@ObsoleteCoroutinesApi
class DefaultGateway(
        private val url: String,
        private val client: HttpClient,
        private val retry: Retry,
        private val rateLimiter: RateLimiter
) : Gateway {

    private val channel = BroadcastChannel<Any>(Channel.CONFLATED)

    override var ping: Duration = Duration.INFINITE

    override val events: Flow<Event> = channel.asFlow().drop(1).filterIsInstance()

    private lateinit var socket: DefaultClientWebSocketSession

    private val state: AtomicRef<State> = atomic(State.Restart(true))

    private val handshakeHandler: HandshakeHandler

    init {
        channel.sendBlocking(Unit)
        val sequence = Sequence()
        SequenceHandler(events, sequence)
        handshakeHandler = HandshakeHandler(events, ::send, sequence)
        HeartbeatHandler(events, ::send, { restart() }, { ping = it }, sequence)
        ReconnectHandler(events) { restart() }
        InvalidSessionHandler(events) { restart(it) }
    }

    override suspend fun start(configuration: GatewayConfiguration) {
        require(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        handshakeHandler.configuration = configuration
        retry.reset()
        state.update { State.Restart(true) } //resetting state
        while (retry.hasNext && state.value is State.Restart) {

            try {
                socket = webSocket(url)
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
                retry.retry()
                continue //can't handle a close code if you've got no socket
            }

            try {
                readSocket()
                retry.reset() //connected and read without problems, resetting retry counter
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
            }

            handleClose()

            if (state.value.retry) retry.retry()
        }

        if (!retry.hasNext) {
            defaultGatewayLogger.warn { "retry limit exceeded, gateway closing" }
        }
    }

    private suspend fun readSocket() {
        socket.incoming.asFlow().filterIsInstance<Frame.Text>().collect { read(it) }
    }

    private suspend fun read(frame: Frame.Text) {
        val json = frame.readText()
        try {
            defaultGatewayLogger.trace { "Gateway <<< $json" }
            Json.nonstrict.parse(Event.Companion, json)?.let { channel.send(it) }
        } catch (exception: Exception) {
            defaultGatewayLogger.error(exception)
        }

    }

    private suspend fun handleClose() {
        val reason = socket.closeReason.await() ?: return
        defaultGatewayLogger.trace { "Gateway closed: ${reason.code} ${reason.message}" }
        val discordReason = GatewayCloseCode.values().firstOrNull { it.code == reason.code } ?: return

        when {
            !discordReason.retry -> {
                state.update { State.ShutDown }
                throw  IllegalStateException("Gateway closed: ${reason.code} ${reason.message}")
            }
            discordReason.resetSession -> {
                state.update { State.Restart(true) }
                channel.send(SessionClose)
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

    private suspend fun webSocket(url: String) = client.webSocketSession { url(url) }

    override suspend fun stop() {
        require(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        channel.send(SessionClose)
        state.update { State.ShutDown }
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    internal suspend fun restart(code: Close = CloseForReconnect) {
        require(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        state.update { State.Restart(false) }
        if (socketOpen) {
            channel.send(code)
            socket.close(CloseReason(1000, "reconnecting"))
        }
    }

    override suspend fun detach() {
        if (state.value is State.Detached) return
        state.update { State.Detached }
        channel.cancel()
        socket.close()
    }

    override suspend fun send(command: Command) {
        require(state.value !is State.Detached) { "The resources of this gateway are detached, create another one" }
        if (!socketOpen) error("call 'start' before sending messages")
        rateLimiter.consume()
        val json = Json.stringify(Command.Companion, command)
        if (command is Identify) defaultGatewayLogger.trace { "Gateway >>> Identify" }
        else defaultGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive

    companion object {

        inline operator fun invoke(builder: DefaultGatewayBuilder.() -> Unit = {}): DefaultGateway =
                DefaultGatewayBuilder().apply(builder).build()


    }
}

internal val GatewayConfiguration.identify get() = Identify(token, IdentifyProperties(os, name, name), false, 50, shard, presence)

internal val os: String get() = System.getProperty("os.name")

/**
 * Enum representation of https://discordapp.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-close-event-codes
 *
 * @param retry Whether the error is caused by the user or by Kord.
 * If we caused it, we should consider restarting the gateway.
 */
internal enum class GatewayCloseCode(val code: Short, val retry: Boolean = true, val resetSession: Boolean = false) {
    /**
     * ¯\_(ツ)_/¯
     */
    Unknown(4000),

    /**
     * We're sending the wrong opCode, this shouldn't happen unless we seriously broke something.
     */
    UnknownOpCode(4001),

    /**
     * We're sending malformed data, this shouldn't happen unless we seriously broke something.
     */
    DecodeError(4002),

    /**
     * We're sending data without starting a session, this shouldn't happen unless we seriously broke something.
     */
    NotAuthenticated(4003),

    /**
     * User send wrong token.
     */
    AuthenticationFailed(4004, false),

    /**
     * We're identifying more than once, this shouldn't happen unless we seriously broke something.
     */
    AlreadyAuthenticated(4005),

    /**
     * Send wrong sequence, restart and reset sequence number.
     */
    InvalidSeq(4007, true, true),

    /**
     * We're sending too fast, this means the user passed a wrongly configured rate limiter, we'll just ignore that though.
     */
    RateLimited(4008),

    /**
     * Timeout, Heartbeat handling is probably at fault, restart and reset sequence number.
     */
    SessionTimeout(4009, true, resetSession = true),

    /**
     * User supplied the wrong sharding info, we can't fix this on our end so we'll just stop.
     */
    InvalidShard(4010),

    /**
     * User didn't supply sharding info when it was required, we can't fix this on our end so we'll just stop.
     */
    ShardingRequired(4011)
}