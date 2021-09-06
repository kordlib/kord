package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.Streams
import dev.kord.voice.gateway.*
import dev.kord.voice.gateway.handler.Handler
import kotlinx.coroutines.flow.Flow

@OptIn(KordVoice::class)
internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : Handler(flow, "HandshakeHandler") {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        on<SessionDescription> {
            streams.key = it.secretKey.toUByteArray().toByteArray()
        }
    }
}