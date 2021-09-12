package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.AudioFrame
import dev.kord.voice.FrameInterceptor
import dev.kord.voice.rtp.AudioPacket
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private val audioFrameSenderLogger = KotlinLogging.logger { }

@KordVoice
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
                        val encryptedPacket = AudioPacket.DecryptedPacket.create(
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