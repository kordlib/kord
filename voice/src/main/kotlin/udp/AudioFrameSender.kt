@file:OptIn(KordVoice::class)
@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private val audioFrameSenderLogger = KotlinLogging.logger { }

data class AudioFrameSenderConfiguration(
    val ssrc: UInt,
    val key: ByteArray,
    val provider: AudioProvider,
    val baseFrameInterceptorContext: FrameInterceptorContextBuilder,
    val interceptorFactory: (FrameInterceptorContext) -> FrameInterceptor
)

interface AudioFrameSender : CoroutineScope {
    /**
     * This should start polling frames from [the audio provider][AudioFrameSenderConfiguration.provider] and
     * send them to Discord.
     */
    suspend fun start(configuration: AudioFrameSenderConfiguration)
}

data class DefaultAudioFrameSenderData(
    val udp: VoiceUdpConnection,
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
        val interceptor = createFrameInterceptor(configuration)
        var sequence: UShort = Random.nextBits(UShort.SIZE_BITS).toUShort()

        val frames = Channel<AudioFrame?>(Channel.RENDEZVOUS)
        with(provider) { launch { provideFrames(frames) } }

        audioFrameSenderLogger.trace { "audio poller starting." }

        try {
            frames.receiveAsFlow()
                .transform { emit(interceptor.intercept(it)) }
                .collect { frame ->
                    if (frame != null) {
                        val encryptedPacket = AudioPacket.DecryptedPacket(
                            sequence = sequence,
                            timestamp = sequence * 960u,
                            ssrc = ssrc,
                            decryptedData = frame.data
                        ).encrypt(key)

                        data.udp.send(encryptedPacket.asByteReadPacket())
                        sequence++
                    }
                }
        } catch (e: Exception) {
            /* we're done polling, nothing to worry about */
        }

        audioFrameSenderLogger.trace { "udp connection closed, stopped polling for audio frames." }
    }
}