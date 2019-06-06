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
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
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

var global: Any? = null

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

    private lateinit var socket: DefaultClientWebSocketSession

    private val restart = atomic(true)

    override suspend fun start(configuration: GatewayConfiguration) {
        while (retry.hasNext && restart.value) {
            close()

            val result = runCatching {
                global = HandshakeHandler(events, ::send, configuration)
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
            val text = json.readText()
            defaultGatewayLogger.trace { "Gateway <<< $json" }

            val event = Json.nonstrict.parse(Event.Companion, text)

            event?.let { channel.send(it) }

            when (event) {
                Reconnect -> close()
                //TODO: Fix this: is Hello -> ticker.tickAt(event.heartbeatInterval) { send(Command.Heartbeat(handshakeHandler.sequence)) }
            }
        }
    }

    private suspend fun webSocket(url: String) = client.webSocketSession(HttpMethod.Get, host = url) {
        this.url.protocol = URLProtocol.WSS
        this.url.port = 443
    }

    private suspend fun handleReason(reason: CloseReason?) {
        if (reason?.code ?: 0 in 4000..5000) {
            close()
            error("${reason?.message}: ${reason?.code}")
        } else {
            restart()
            defaultGatewayLogger.info { "gateway closed with a non-error code ${reason?.code}, retrying connection" }
        }
    }

    override suspend fun close() {
        ticker.stop()
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
        restart.update { false }
    }

    private suspend fun restart() {
        ticker.stop()
        if (socketOpen) socket.close(CloseReason(1000, "reconnecting"))
        restart.update { true }
    }

    override suspend fun send(command: Command) {
        if (!socketOpen) error("call 'start' before sending messages")
        rateLimiter.consume()
        val json = Json.stringify(Command.Companion, command)
        defaultGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend
}

internal val os: String get() = System.getProperty("os.name")