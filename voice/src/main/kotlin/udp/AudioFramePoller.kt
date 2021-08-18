@file:OptIn(KordVoice::class)

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.AudioProvider
import dev.kord.voice.FrameInterceptor
import dev.kord.voice.FrameInterceptorContext
import dev.kord.voice.FrameInterceptorContextBuilder
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.TimeSource

private val audioFramePollerLogger = KotlinLogging.logger { }

internal data class AudioFramePollerConfiguration(
    val udp: DiscordUdpConnection,
    val ssrc: Int,
    val key: List<Byte>,
    val provider: AudioProvider,
    val baseFrameInterceptorContext: FrameInterceptorContextBuilder,
    val interceptorFactory: (FrameInterceptorContext) -> FrameInterceptor
)

internal class AudioFramePollerConfigurationBuilder {
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
    timeSource: TimeSource,
    pollerDispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + pollerDispatcher + CoroutineName("Audio Frame Poller")

    private val timeMark = timeSource.markNow()

    private val currentNanoTime get() = timeMark.elapsedNow().inWholeNanoseconds

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
            var nextFrameTimestamp = currentNanoTime
            val key = key.toByteArray()

            audioFramePollerLogger.trace { "audio poller starting." }

            while (udp.isOpen) {
                val frame = interceptor.intercept(provider.provide())

                var packet: AudioPacket? = null

                if (frame != null) {
                    packet = AudioPacket(frame, sequence, sequence * 960, ssrc)
                    packet.encrypt(key)
                }

                nextFrameTimestamp += Duration.milliseconds(20).inWholeNanoseconds
                sequence++

                delay(Duration.nanoseconds(max(0, nextFrameTimestamp - currentNanoTime)).inWholeMilliseconds)

                if (packet != null) {
                    if (udp.isOpen) udp.send(Datagram(packet.asByteReadPacket(), udp.server))
                }
            }

            audioFramePollerLogger.trace { "udp connection closed, stopped polling for audio frames." }
        }
    }
}