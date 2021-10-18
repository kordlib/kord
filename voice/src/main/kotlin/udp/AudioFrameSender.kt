@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.AudioProvider
import dev.kord.voice.FrameInterceptor
import dev.kord.voice.FrameInterceptorContext
import dev.kord.voice.FrameInterceptorContextBuilder
import io.ktor.util.network.*

@KordVoice
data class AudioFrameSenderConfiguration(
    val server: NetworkAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val provider: AudioProvider,
    val baseFrameInterceptorContext: FrameInterceptorContextBuilder,
    val interceptorFactory: (FrameInterceptorContext) -> FrameInterceptor
)

@KordVoice
interface AudioFrameSender {
    /**
     * This should start polling frames from [the audio provider][AudioFrameSenderConfiguration.provider] and
     * send them to Discord.
     */
    suspend fun start(configuration: AudioFrameSenderConfiguration)
}