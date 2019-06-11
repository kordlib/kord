package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.handler.HandshakeHandler
import com.gitlab.hopebaron.websocket.handler.HeartbeatHandler
import com.gitlab.hopebaron.websocket.handler.ReconnectHandler
import com.gitlab.hopebaron.websocket.handler.SequenceHandler
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val defaultGatewayLogger = KotlinLogging.logger { }

@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGateway(
        private val url: String,
        private val client: HttpClient,
        private val retry: Retry,
        private val rateLimiter: RateLimiter
) : Gateway {

    private val channel = BroadcastChannel<Event>(Channel.CONFLATED)

    override val events: Flow<Event> = channel.asFlow()

    private lateinit var socket: DefaultClientWebSocketSession

    private val restart = atomic(true)

    private val handshakeHandler: HandshakeHandler

    init {
        val sequence = Sequence()
        handshakeHandler = HandshakeHandler(events, ::send, sequence)
        HeartbeatHandler(events, ::send, ::restart, sequence)
        ReconnectHandler(events, ::restart)
        SequenceHandler(events, sequence)
    }

    override suspend fun start(configuration: GatewayConfiguration) {
        handshakeHandler.configuration = configuration
        retry.reset()
        while (retry.hasNext && restart.value) {
            try {
                socket = webSocket(url)

                flow {
                    val iterator = socket.incoming.iterator()
                    try {
                        while (iterator.hasNext()) emit(iterator.next())
                    } catch (ignore: CancellationException) {
                        //reading was stopped from somewhere else, ignore
                    }
                }.filterIsInstance<Frame.Text>().collect { frame ->
                    retry.reset()

                    val json = frame.readText()
                    defaultGatewayLogger.trace { "Gateway <<< $json" }

                    Json.nonstrict.parse(Event.Companion, json)?.let { channel.send(it) }
                }
            } catch (exception: Exception) {
                defaultGatewayLogger.error(exception)
            }

            if (!::socket.isInitialized) continue //failed to make a web socket the first time, let's try again
            close()

            val reason = socket.closeReason.await() //TODO this doesn't work when we close the websocket ourselves
            if (reason?.code ?: 0 in 4000..5000) error("${reason?.message}: ${reason?.code}")

            defaultGatewayLogger.info { "gateway closed with a non-error code ${reason?.code}, retrying connection" }
            restart.update { true }
        }
    }


    private suspend fun webSocket(url: String) = client.webSocketSession(HttpMethod.Get, host = url) {
        this.url.protocol = URLProtocol.WSS
        this.url.port = 443
    }

    override suspend fun close() {
        if (socketOpen) socket.close(CloseReason(1000, "leaving"))
        restart.update { false }
        channel.send(SessionClose)
    }

    suspend fun restart() {
        if (socketOpen) socket.close(CloseReason(1000, "reconnecting"))
        restart.update { true }
        channel.send(CloseForReconnect)
    }

    override suspend fun send(command: Command) {
        if (!socketOpen) error("call 'start' before sending messages")
        rateLimiter.consume()
        val json = Json.stringify(Command.Companion, command)
        if (command is Identify) defaultGatewayLogger.trace { "Gateway >>> Identify" }
        else defaultGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(Frame.Text(json))
    }

    private val socketOpen get() = ::socket.isInitialized && !socket.outgoing.isClosedForSend && !socket.incoming.isClosedForReceive
}

internal val GatewayConfiguration.identify get() = Identify(token, IdentifyProperties(os, name, name), false, 50, shard, presence)

internal val os: String get() = System.getProperty("os.name")