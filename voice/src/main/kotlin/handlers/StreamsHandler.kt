package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.gateway.Close
import dev.kord.voice.gateway.Ready
import dev.kord.voice.gateway.SessionDescription
import dev.kord.voice.gateway.VoiceEvent
import dev.kord.voice.gateway.handler.GatewayEventHandler
import dev.kord.voice.streams.Streams
import io.ktor.util.network.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(KordVoice::class)
internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : GatewayEventHandler(flow, "HandshakeHandler") {
    private val server: AtomicRef<NetworkAddress?> = atomic(null)

    private var streamsJob: Job? by atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Ready> {
            server.value = NetworkAddress(it.ip, it.port)
        }

        on<SessionDescription> {
            streamsJob?.cancel()
            streamsJob = launch { streams.listen(it.secretKey.toUByteArray().toByteArray(), server.value!!) }
        }

        on<Close> {
            streamsJob?.cancel()
        }
    }
}