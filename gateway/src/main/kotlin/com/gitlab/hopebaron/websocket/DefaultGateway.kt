package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.ratelimit.RateLimiter
import com.gitlab.hopebaron.websocket.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.util.error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.mapNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val defaultGatewayLogger = KotlinLogging.logger { }

@FlowPreview
@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGateway(
        private val url: String,
        private val client: HttpClient,
        private val retry: Retry,
        private val rateLimiter: RateLimiter
) : Gateway {

    private val ticker = Ticker()

    private val channel = BroadcastChannel<Event>(Channel.CONFLATED)

    override val events: Flow<Event>
        get() = channel.asFlow()

    private val handshakeHandler = HandshakeHandler(events, this::send)

    private lateinit var socket: DefaultClientWebSocketSession

    override suspend fun start(configuration: GatewayConfiguration) {
        while (retry.hasNext) {
            close()

            val result = runCatching {
                handshakeHandler.start(configuration)
                socket = webSocket(url)
                readJson(socket.incoming.mapNotNull { it as? Frame.Text })
            }

            result.onFailure {
                defaultGatewayLogger.error(it)
                handleReason(socket.closeReason.await())
                retry.retry()
            }
        }
    }

    private suspend fun readJson(incoming: ReceiveChannel<Frame.Text>) {
        for (json in incoming) {
            retry.reset()
            val payload = Json.nonstrict.parse(Payload.serializer(), json.readText())

            handshakeHandler.sequence = payload.sequence ?: handshakeHandler.sequence
            val event = payload.event
            event?.let { channel.send(it) }

            when (event) {
                Reconnect -> close()
                is Hello -> ticker.tickAt(event.heartbeatInterval) { send(Command.Heartbeat(handshakeHandler.sequence)) }
            }
        }
    }

    private suspend fun webSocket(url: String) = client.webSocketSession(HttpMethod.Get, host = url) {
        this.url.protocol = URLProtocol.WSS
        this.url.port = 443
    }

    private fun handleReason(reason: CloseReason?) {
        if (reason?.code ?: 0 in 4000..5000) error("${reason?.message}: ${reason?.code}")
        else defaultGatewayLogger.info { "gateway closed with a non-error code ${reason?.code}, retrying connection" }
    }

    override suspend fun close() {
        handshakeHandler.stop()
        ticker.stop()
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
    }

    override suspend fun send(command: Command) {
        rateLimiter.consume()
        println(">>> $command")
        val json = Json.stringify(Command.Companion, command)
        if (socketOpen) socket.send(Frame.Text(json))
        else error("call 'start' before sending messages")
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend
}

internal val os: String get() = System.getProperty("os.name")