package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.AudioFrame
import dev.kord.voice.FrameInterceptor
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private val audioFrameSenderLogger = KotlinLogging.logger { }

@KordVoice
data class DefaultAudioFrameSenderData(
    val udp: VoiceUdpSocket,
    val dispatcher: CoroutineDispatcher
)

@KordVoice
class DefaultAudioFrameSender(
    val data: DefaultAudioFrameSenderData
) : AudioFrameSender {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + data.dispatcher + CoroutineName("Default Audio Frame Poller")

    private fun createFrameInterceptor(configuration: AudioFrameSenderConfiguration): FrameInterceptor =
        with(configuration) {
            val builder = baseFrameInterceptorContext
            builder.ssrc = ssrc
            return interceptorFactory(builder.build()) // we should assume that everything else is set before-hand in the base builder
        }

    override suspend fun start(configuration: AudioFrameSenderConfiguration) = with(configuration) {
        val interceptor: FrameInterceptor = createFrameInterceptor(configuration)
        var sequence: UShort = Random.nextBits(UShort.SIZE_BITS).toUShort()

        val packetProvider = DefaultAudioPackerProvider(key)

        val frames = Channel<AudioFrame?>(Channel.RENDEZVOUS)
        with(provider) { launch { provideFrames(frames) } }

        audioFrameSenderLogger.trace { "audio poller starting." }

        try {
            for (frame in frames) {
                val consumedFrame = interceptor.intercept(frame) ?: continue
                val packet = packetProvider.provide(sequence, sequence * 960u, ssrc, consumedFrame.data)
                data.udp.send(Datagram(ByteReadPacket(packet.data, packet.dataStart, packet.viewSize), server))
                sequence++
            }
        } catch (e: Exception) {
            audioFrameSenderLogger.trace(e) { "poller stopped with reason" }
            /* we're done polling, nothing to worry about */
        }
    }
}