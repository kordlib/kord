package dev.kord.voice.handlers

import dev.kord.common.entity.Snowflake
import dev.kord.voice.gateway.*
import dev.kord.voice.gateway.handler.GatewayEventHandler
import dev.kord.voice.streams.Streams
import io.ktor.network.sockets.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : GatewayEventHandler(flow, "HandshakeHandler") {
    private val server: AtomicRef<SocketAddress?> = atomic(null)

    private var streamsJob: Job? by atomic(null)

    private val s2u = atomic(persistentHashMapOf<UInt, Snowflake>())

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Speaking> { speaking ->
            s2u.update { it.put(speaking.ssrc, speaking.userId) }
        }

        on<Ready> {
            server.value = InetSocketAddress(it.ip, it.port)
        }

        on<SessionDescription> {
            streamsJob?.cancel()
            streamsJob =
                launch { streams.listen(it.secretKey.toUByteArray().toByteArray(), server.value!!, s2u = { s2u.value[it] }) }
        }

        on<Close> {
            streamsJob?.cancel()
        }
    }
}
