@file:OptIn(KordVoice::class)

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

private val audioFramePollerLogger = KotlinLogging.logger { }

internal data class AudioFramePollerConfiguration(
    val udp: DiscordUdpConnection,
    val ssrc: Int,
    val key: List<Byte>,
    val provider: AudioProvider,
    val baseFrameInterceptorContext: FrameInterceptorContextBuilder,
    val interceptorFactory: (FrameInterceptorContext) -> FrameInterceptor
)

internal class AudioFramePollerConfigurationBuilder() {
    var udp: DiscordUdpConnection? = null
    var ssrc: Int by Delegates.notNull()
    var key: List<Byte> by Delegates.notNull()
    var provider: AudioProvider by Delegates.notNull()
    var baseFrameInterceptorContext: FrameInterceptorContextBuilder by Delegates.notNull()
    var interceptorFactory: (FrameInterceptorContext) -> FrameInterceptor by Delegates.notNull()

    fun build(): AudioFramePollerConfiguration =
        AudioFramePollerConfiguration(udp!!, ssrc, key, provider, baseFrameInterceptorContext, interceptorFactory)
}

internal class AudioFramePoller(
    pollerDispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + pollerDispatcher + CoroutineName("Audio Frame Poller")

    private fun createFrameInterceptor(configuration: AudioFramePollerConfiguration): FrameInterceptor =
        with(configuration) {
            val builder = configuration.baseFrameInterceptorContext
            builder.ssrc = configuration.ssrc
            return interceptorFactory(builder.build()) // we should assume that everything else is set before-hand in the base builder
        }

    fun start(configuration: AudioFramePollerConfiguration) = with(configuration) {
        launch {
            val interceptor = createFrameInterceptor(configuration)
            var sequence: Short = 0
            val key = key.toByteArray()

            val frames = Channel<AudioFrame?>(Channel.RENDEZVOUS)
            with(provider) { launch { provideFrames(frames) } }

            audioFramePollerLogger.trace { "audio poller starting." }

            try {
                frames.receiveAsFlow()
                    .transform { emit(interceptor.intercept(it)) }
                    .collect { frame ->
                        if (frame != null) {
                            val packet = AudioPacket(frame, sequence, sequence * 960, ssrc)
                            packet.encrypt(key)

                            udp.send(Datagram(packet.asByteReadPacket(), udp.server))
                            sequence++
                        }
                    }
            } catch (e: Exception) {
                /* we're done polling, nothing to worry about */
            }

            audioFramePollerLogger.trace { "udp connection closed, stopped polling for audio frames." }
        }
    }
}