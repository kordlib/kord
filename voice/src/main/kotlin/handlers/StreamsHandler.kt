package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.gateway.*
import dev.kord.voice.gateway.handler.Handler
import dev.kord.voice.streams.Streams
import io.ktor.util.network.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow

@OptIn(KordVoice::class)
internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : Handler(flow, "HandshakeHandler") {
    private val server: AtomicRef<NetworkAddress?> = atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        on<Ready> {
            server.value = NetworkAddress(it.ip, it.port)
        }

        on<SessionDescription> {
            streams.listen(it.secretKey.toUByteArray().toByteArray(), server.value!!)
        }
    }
}