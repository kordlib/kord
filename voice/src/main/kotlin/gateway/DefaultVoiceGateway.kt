package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.retry.Retry
import dev.kord.voice.gateway.handler.HandshakeHandler
import dev.kord.voice.gateway.handler.HeartbeatHandler
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.logging.*
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
import mu.KotlinLogging
import kotlin.time.Duration

private val defaultVoiceGatewayLogger = KotlinLogging.logger { }

private sealed class State(val retry: Boolean) {
    object Stopped : State(false)
    class Running(retry: Boolean) : State(retry)
}

@KordVoice
public data class DefaultVoiceGatewayData(
    val selfId: Snowflake,
    val guildId: Snowflake,
    val sessionId: String,
    val client: HttpClient,
    val reconnectRetry: Retry,
    val eventFlow: MutableSharedFlow<VoiceEvent>
)

/**
 * The default Voice Gateway implementation of Kord, using an [HttpClient] for the underlying websocket.
 */
@KordVoice
public class DefaultVoiceGateway(
    private val data: DefaultVoiceGatewayData
) : VoiceGateway {
    override val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + CoroutineName("kord-voice-gateway[$${data.guildId.value}]"))

    private lateinit var socket: DefaultClientWebSocketSession

    override val events: SharedFlow<VoiceEvent> = data.eventFlow

    private val state: AtomicRef<State> = atomic(State.Stopped)

    private val _ping = MutableStateFlow<Duration?>(null)
    override val ping: StateFlow<Duration?> get() = _ping

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val stateMutex = Mutex()

    private val handshakeHandler: HandshakeHandler

    init {
        handshakeHandler = HandshakeHandler(events, data, ::trySend)
        with(scope) {
            launch { handshakeHandler.start() }
            launch { HeartbeatHandler(events, ::trySend, { _ping.value = it }).start() }
        }
    }

    // prevent race conditions caused by suspending due to the reconnectRetry
    private val connectMutex = Mutex(locked = false)

    override suspend fun start(configuration: VoiceGatewayConfiguration): Unit = connectMutex.withLock {
        resetState(configuration)

        while (data.reconnectRetry.hasNext && state.value is State.Running) {
            try {
                socket = webSocket(configuration.endpoint)
            } catch (exception: Exception) {
                if (exception is CancellationException) break

                defaultVoiceGatewayLogger.error(exception)
                if (exception is java.nio.channels.UnresolvedAddressException) {
                    data.eventFlow.emit(Close.Timeout)
                }

                data.reconnectRetry.retry()
                continue //can't handle a close code if you've got no socket
            }

            defaultVoiceGatewayLogger.trace { "connected to the voice socket (${configuration.endpoint}) for guild (${data.guildId.value})!" }

            try {
                readSocket()
            } catch (exception: CancellationException) {
                defaultVoiceGatewayLogger.trace(exception) { "voice gateway stopped" }
            } catch (exception: Exception) {
                defaultVoiceGatewayLogger.error(exception) { "voice gateway stopped"}
            }

            defaultVoiceGatewayLogger.trace { "voice gateway connection closing" }

            try {
                handleClose()
            } catch (exception: CancellationException) {
                defaultVoiceGatewayLogger.trace(exception) { "" }
            } catch (exception: Exception) {
                defaultVoiceGatewayLogger.error(exception)
            }

            defaultVoiceGatewayLogger.trace { "handled voice gateway connection closed" }

            if (state.value.retry) data.reconnectRetry.retry()
            else data.eventFlow.emit(Close.RetryLimitReached)
        }

        _ping.value = null
        if (!data.reconnectRetry.hasNext) {
            defaultVoiceGatewayLogger.warn { "retry limit exceeded, gateway closing" }
        }
    }

    private suspend fun readSocket() {
        socket.incoming.asFlow().buffer(Channel.UNLIMITED).collect {
            when (it) {
                is Frame.Binary, is Frame.Text -> read(it)
                else -> { /*ignore*/
                }
            }
        }
    }

    private suspend fun read(frame: Frame) {
        val json = String(frame.data, Charsets.UTF_8)

        try {
            val event = jsonParser.decodeFromString(VoiceEvent.DeserializationStrategy, json)

            if (event is SessionDescription)
                defaultVoiceGatewayLogger.trace { "Voice Gateway <<< SESSION_DESCRIPTION" }
            else
                defaultVoiceGatewayLogger.trace { "Voice Gateway <<< $json" }

            if (event == null) return

            data.eventFlow.emit(event)
        } catch (exception: Exception) {
            defaultVoiceGatewayLogger.error(exception)
        }
    }

    private suspend fun webSocket(url: String) = data.client.webSocketSession {
        url(url)

        // workaround until https://youtrack.jetbrains.com/issue/KTOR-4419 is fixed
        // otherwise the voice connection will die and fail to reconnect
        timeout {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
    }

    private suspend fun resetState(configuration: VoiceGatewayConfiguration) = stateMutex.withLock {
        @Suppress("UNUSED_VARIABLE")
        val exhaustive = when (state.value) { //exhaustive state checking
            is State.Running -> throw IllegalStateException("The Gateway is already running, call stop() first.")
            State.Stopped -> Unit
        }

        handshakeHandler.configuration = configuration
        data.reconnectRetry.reset()
        state.update { State.Running(true) } // resetting state
    }


    override suspend fun send(command: Command): Unit = stateMutex.withLock {
        sendUnsafe(command)
    }

    private suspend fun trySend(command: Command) = stateMutex.withLock {
        if (state.value !is State.Running) return@withLock
        sendUnsafe(command)
    }

    private suspend fun sendUnsafe(command: Command) {
        val json = Json.encodeToString(Command.SerializationStrategy, command)
        if (command is Identify) {
            defaultVoiceGatewayLogger.trace {
                val copy = command.copy(token = "token")
                "Voice Gateway >>> ${Json.encodeToString(Command.SerializationStrategy, copy)}"
            }
        } else if (command is SelectProtocol) {
            defaultVoiceGatewayLogger.trace {
                val copy = command.copy(data = command.data.copy(address = "ip"))
                "Voice Gateway >>> ${Json.encodeToString(Command.SerializationStrategy, copy)}"
            }
        } else {
            defaultVoiceGatewayLogger.trace { "Voice Gateway >>> $json" }
        }
        socket.send(Frame.Text(json))
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive

    override suspend fun detach() {
        stop()
        data.client.close()
        scope.cancel()
    }

    override suspend fun stop() {
        data.eventFlow.emit(Close.UserClose)
        state.update { State.Stopped }
        _ping.value = null
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    private suspend fun handleClose() {
        val reason = withTimeoutOrNull(1500) {
            socket.closeReason.await()
        } ?: return

        defaultVoiceGatewayLogger.trace { "Voice Gateway (${data.guildId.value}) closed: ${reason.code} ${reason.message}" }
        val discordReason = VoiceGatewayCloseCode.of(reason.code.toInt())

        data.eventFlow.emit(Close.DiscordClose(discordReason, discordReason.retry))

        if (!discordReason.retry) {
            state.update { State.Stopped }
            if (discordReason.exceptional) {
                throw IllegalStateException("Voice Gateway (${data.guildId.value}) closed: ${reason.code} ${reason.message}")
            }
        }
    }
}

internal val VoiceGatewayCloseCode.retry
    get() = when (this) { //this statement is intentionally structured to ensure we consider the retry for every new code
        VoiceGatewayCloseCode.UnknownOpcode -> true
        VoiceGatewayCloseCode.FailedToDecodePayload -> true
        VoiceGatewayCloseCode.NotAuthenticated -> true
        VoiceGatewayCloseCode.AuthenticationFailed -> false
        VoiceGatewayCloseCode.AlreadyAuthenticated -> true
        VoiceGatewayCloseCode.SessionNoLongerValid -> false
        VoiceGatewayCloseCode.SessionTimeout -> true
        VoiceGatewayCloseCode.ServerNotFound -> false
        VoiceGatewayCloseCode.UnknownProtocol -> false
        VoiceGatewayCloseCode.Disconnect -> false
        VoiceGatewayCloseCode.VoiceServerCrashed -> true
        VoiceGatewayCloseCode.UnknownEncryptionMode -> false
        is VoiceGatewayCloseCode.Unknown -> true
    }

/**
 * Whether this close code is a cause for concern, and or if it's probably caused by kord.
 */
internal val VoiceGatewayCloseCode.exceptional
    get() = when (this) {
        VoiceGatewayCloseCode.UnknownOpcode -> true
        VoiceGatewayCloseCode.FailedToDecodePayload -> true
        VoiceGatewayCloseCode.NotAuthenticated -> true
        VoiceGatewayCloseCode.AuthenticationFailed -> true
        VoiceGatewayCloseCode.AlreadyAuthenticated -> true
        VoiceGatewayCloseCode.SessionNoLongerValid -> false
        VoiceGatewayCloseCode.SessionTimeout -> false
        VoiceGatewayCloseCode.ServerNotFound -> true
        VoiceGatewayCloseCode.UnknownProtocol -> true
        VoiceGatewayCloseCode.Disconnect -> false
        VoiceGatewayCloseCode.VoiceServerCrashed -> false
        VoiceGatewayCloseCode.UnknownEncryptionMode -> true
        is VoiceGatewayCloseCode.Unknown -> false
    }

private fun <T> ReceiveChannel<T>.asFlow() = flow {
    try {
        for (value in this@asFlow) emit(value)
    } catch (ignore: CancellationException) {
        //reading was stopped from somewhere else, ignore
    }
}
