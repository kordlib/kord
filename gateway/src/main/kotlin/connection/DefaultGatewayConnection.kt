package dev.kord.gateway.connection

import dev.kord.gateway.*
import dev.kord.gateway.connection.GatewayConnection.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import mu.KLogger
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.nio.charset.StandardCharsets
import java.util.zip.InflaterOutputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark
import io.ktor.websocket.CloseReason as KtorCloseReason

/**
 * Default implementation of [GatewayConnection].
 */
internal class DefaultGatewayConnection : GatewayConnection {

    override val ping: MutableStateFlow<Duration?> = MutableStateFlow(null)
    private lateinit var inflater: FrameInflater
    private lateinit var data: Data
    private lateinit var session: DefaultClientWebSocketSession
    private lateinit var scope: CoroutineScope
    private val atomicPossiblyZombie: AtomicBoolean = atomic(false)
    private var possiblyZombie: Boolean by atomicPossiblyZombie
    private val atomicSequence: AtomicRef<Int?> = atomic(null)
    private val sequence: Int? by atomicSequence
    private val log: KLogger = KotlinLogging.logger { }
    private val atomicHeartbeatTimeMark: AtomicRef<ValueTimeMark> = atomic(TimeSource.Monotonic.markNow())
    private var heartbeatTimeMark: ValueTimeMark by atomicHeartbeatTimeMark
    private val atomicReceivedHello: AtomicBoolean = atomic(false)
    private val atomicReadyData: AtomicRef<ReadyData?> = atomic(null)
    private val atomicReconnectRequested: AtomicBoolean = atomic(false)
    private val atomicInvalidSession: AtomicRef<InvalidSession?> = atomic(null)
    private val atomicState: AtomicRef<State> = atomic(State.Uninitialized)
    private val atomicManualClose: AtomicBoolean = atomic(false)
    private val atomicHeartbeatJob: AtomicRef<Job?> = atomic(null)
    private val sessionToken: String
        get() = when (val sessionData = data.session) {
            is Session.New -> sessionData.identify.token
            is Session.Resumed -> sessionData.resume.token
        }

    override suspend fun open(data: Data): CloseReason {
        if (!atomicState.compareAndSet(State.Uninitialized, State.Opening)) errorInvalidState()
        this.data = data
        val url = Url(data.uri)
        val isZLibCompressed = url.parameters.contains("compress", "zlib-stream")
        inflater = if (isZLibCompressed) FrameInflater.ZLib() else FrameInflater.None

        coroutineScope {
            scope = this
            session = data.client.webSocketSession {
                url(url)
            }
            processIncoming()
            atomicHeartbeatJob.value?.cancel()
        }

        val reason = resolveCloseReason()
        if (!atomicState.compareAndSet(expect = State.Closing, update = State.Closed)) errorInvalidState()
        return reason
    }

    private suspend fun resolveCloseReason(): CloseReason {
        val resume = atomicReadyData.value?.let { Resume(sessionToken, it.sessionId, sequence ?: 0) }

        val isReconnectRequested = atomicReconnectRequested.value
        if (isReconnectRequested) {
            return if (resume != null) {
                CloseReason.ResumableReconnect(resume)
            } else CloseReason.Reconnect
        }

        val invalidSessionData = atomicInvalidSession.value
        if (invalidSessionData != null) {
            return if (invalidSessionData.resumable && resume != null) {
                CloseReason.ResumableInvalidSession(resume)
            } else CloseReason.InvalidSession
        }

        val isClosedManually = atomicManualClose.value
        if (isClosedManually) return CloseReason.Manual

        val sessionCloseReason = session.closeReason.await()
        if (sessionCloseReason != null) {
            return CloseReason.Plain(sessionCloseReason.code.toInt(), sessionCloseReason.message, resume)
        }

        return CloseReason.Error(IllegalStateException("Could not resolve close reason."))
    }

    private suspend fun processIncoming() {
        inflater.use { inflater ->
            for (frame in session.incoming) when (frame) {
                is Frame.Binary, is Frame.Text -> {
                    processInflatedFrame(inflater.inflate(frame))
                }

                else -> {} // Ignore other, they are handled by the session.
            }
        }
    }

    private suspend fun processInflatedFrame(byteArray: ByteArray) {
        val jsonString = byteArray.toString(StandardCharsets.UTF_8)
        log.info { "Gateway <<< $jsonString" }
        runCatching {
            data.json.decodeFromString(Event.DeserializationStrategy, jsonString)?.also { processEvent(it) }
        }.onFailure {
            log.catching(it)
        }.onSuccess { event ->
            (event as? DispatchEvent)?.sequence?.let { atomicSequence.value = it }
        }
    }

    private suspend fun processEvent(event: Event) {
        when (event) {
            HeartbeatACK -> processHeartbeatACK()
            is Heartbeat -> scope.launch { sendHeartbeat() }
            is Hello -> processHello(event)
            is Ready -> processReady(event)
            Reconnect -> atomicReconnectRequested.compareAndSet(expect = false, update = true)
            is InvalidSession -> atomicInvalidSession.compareAndSet(expect = null, update = event)
            is DispatchEvent, is Close -> data.eventFlow.emit(event)
        }
    }

    private fun processHeartbeatACK() {
        ping.value = atomicHeartbeatTimeMark.value.elapsedNow()
        possiblyZombie = false
    }

    private fun processReady(ready: Ready) {
        val hasReceivedReadyBefore = !atomicReadyData.compareAndSet(expect = null, update = ready.data)
        if (hasReceivedReadyBefore) {
            log.warn { "Received more than one Ready event." }
            return
        }
    }

    private fun processHello(hello: Hello) {
        data.reconnectRetry.reset()

        val hasReceivedHelloBefore = !atomicReceivedHello.compareAndSet(expect = false, update = true)
        if (hasReceivedHelloBefore) {
            log.warn { "Received more than one Hello opcode." }
        } else {
            if (!atomicState.compareAndSet(expect = State.Opening, update = State.Open)) errorInvalidState()
            atomicHeartbeatJob.compareAndSet(null, scope.launch { heartBeating(hello.heartbeatInterval.seconds) })
        }

        val resumeOrIdentify = when (val sessionData = data.session) {
            is Session.New -> sessionData.identify
            is Session.Resumed -> sessionData.resume
        }
        scope.launch {
            data.identifyRateLimiter.consume(data.shard.index, data.eventFlow)
            send(resumeOrIdentify)
        }
    }

    private suspend fun heartBeating(interval: Duration) {
        val coroutineContext = currentCoroutineContext()
        while (atomicState.value == State.Open && coroutineContext.isActive) {
            val isZombie = !atomicPossiblyZombie.compareAndSet(expect = false, update = true)
            if (isZombie) {
                atomicReconnectRequested.compareAndSet(expect = false, update = true)
                session.close(CLOSE_REASON_RECONNECTING)
                break
            }
            sendHeartbeat()
            delay(interval)
        }
    }

    private suspend fun sendHeartbeat() {
        data.sendRateLimiter.consume()
        val jsonString = data.json.encodeToString(Command.SerializationStrategy, Command.Heartbeat(sequence))
        heartbeatTimeMark = TimeSource.Monotonic.markNow()
        session.send(jsonString)
    }

    override suspend fun send(command: Command) {
        atomicState.loop { currentState ->
            when (currentState) {
                State.Open -> {
                    data.sendRateLimiter.consume()
                    val jsonString = data.json.encodeToString(Command.SerializationStrategy, command)

                    log.info {
                        val credentialFreeCopy = when (command) {
                            is Identify -> command.copy(token = "Hidden")
                            is Resume -> command.copy(token = "Hidden")
                            else -> null
                        }
                        val credentialFreeJson = if (credentialFreeCopy != null) {
                            data.json.encodeToString(Command.SerializationStrategy, credentialFreeCopy)
                        } else jsonString

                        "Gateway >>> $credentialFreeJson"
                    }

                    session.send(jsonString)
                    return
                }

                State.Opening -> yield()
                State.Closing, State.Closed -> errorInvalidState()
                State.Uninitialized -> errorInvalidState()
            }
        }
    }

    override suspend fun close() {
        if (!atomicState.compareAndSet(expect = State.Open, update = State.Closing)) errorInvalidState()
        atomicManualClose.compareAndSet(expect = false, update = true)
        session.close(CLOSE_REASON_LEAVING)
    }

    private fun errorInvalidState(): Nothing {
        when (atomicState.value) {
            State.Uninitialized -> error("Connection is not initialized.")
            State.Opening -> error("Connection is opening.")
            State.Open -> error("Connection is already open.")
            State.Closing -> error("Connection is closing.")
            State.Closed -> error("Connection is closed.")
        }
    }

    private interface FrameInflater : Closeable {

        fun inflate(frame: Frame): ByteArray

        object None : FrameInflater {
            override fun inflate(frame: Frame): ByteArray = frame.data
            override fun close() {}
        }

        class ZLib : FrameInflater {

            private val buffer = ByteArrayOutputStream()
            private val inflaterOutput = InflaterOutputStream(buffer)

            override fun inflate(frame: Frame): ByteArray {
                inflaterOutput.apply {
                    write(frame.data)
                    flush()
                }
                val inflated = buffer.toByteArray()
                buffer.reset()
                return inflated
            }

            override fun close() {
                inflaterOutput.close()
                buffer.reset()
            }
        }
    }

    private enum class State { Uninitialized, Opening, Open, Closing, Closed }

    private companion object {

        private val CLOSE_REASON_LEAVING = KtorCloseReason(code = KtorCloseReason.Codes.NORMAL, message = "Leaving")
        private val CLOSE_REASON_RECONNECTING = KtorCloseReason(code = 4900, message = "Reconnecting")
    }
}
