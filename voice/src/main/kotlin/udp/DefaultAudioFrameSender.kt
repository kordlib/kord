package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.AudioFrame
import dev.kord.voice.AudioProvider
import dev.kord.voice.FrameInterceptor
import dev.kord.voice.encryption.strategies.NonceStrategy
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.random.Random

private val audioFrameSenderLogger = KotlinLogging.logger { }

@KordVoice
public data class DefaultAudioFrameSenderData(
    val udp: VoiceUdpSocket,
    val interceptor: FrameInterceptor,
    val provider: AudioProvider,
    val nonceStrategy: NonceStrategy,
)

@KordVoice
public class DefaultAudioFrameSender(
    public val data: DefaultAudioFrameSenderData
) : AudioFrameSender {
    override suspend fun start(configuration: AudioFrameSenderConfiguration): Unit = coroutineScope {
        var sequence: UShort = Random.nextBits(UShort.SIZE_BITS).toUShort()

        val packetProvider = DefaultAudioPacketProvider(configuration.key, data.nonceStrategy)

        val frames = Channel<AudioFrame?>(Channel.RENDEZVOUS)
        with(data.provider) { launch { provideFrames(frames) } }

        audioFrameSenderLogger.trace { "audio poller starting." }

        try {
            with(data.interceptor) {
                frames.consumeAsFlow()
                    .intercept(configuration.interceptorConfiguration)
                    .filterNotNull()
                    .map { packetProvider.provide(sequence, sequence * 960u, configuration.ssrc, it.data) }
                    .map { Datagram(ByteReadPacket(it.data, it.dataStart, it.viewSize), configuration.server) }
                    .onEach(data.udp::send)
                    .onEach { sequence++ }
                    .collect()
            }
        } catch (e: Exception) {
            audioFrameSenderLogger.trace(e) { "poller stopped with reason" }
            /* we're done polling, nothing to worry about */
        }
    }
}