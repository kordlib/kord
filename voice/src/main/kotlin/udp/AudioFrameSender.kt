@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.FrameInterceptorConfiguration
import io.ktor.network.sockets.*

@KordVoice
public data class AudioFrameSenderConfiguration(
    val server: SocketAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val interceptorConfiguration: FrameInterceptorConfiguration
)

@KordVoice
public interface AudioFrameSender {
    /**
     * Starts polling frames from [the audio provider][DefaultAudioFrameSenderData.provider] and
     * sends them to Discord.
     */
    public suspend fun start(configuration: AudioFrameSenderConfiguration)
}
