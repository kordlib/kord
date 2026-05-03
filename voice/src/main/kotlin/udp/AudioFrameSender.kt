@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.EncryptionMode
import dev.kord.voice.FrameInterceptorConfiguration
import dev.kord.voice.dave.DaveProtocol
import io.ktor.network.sockets.*

@KordVoice
public data class AudioFrameSenderConfiguration(
    val server: SocketAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val interceptorConfiguration: FrameInterceptorConfiguration,
    val encryptionMode: EncryptionMode = EncryptionMode.AeadAes256GcmRtpSize,
    val daveProtocol: DaveProtocol = dev.kord.voice.dave.NoOpDaveProtocol
)

@KordVoice
public interface AudioFrameSender {
    /**
     * Starts polling frames from [the audio provider][DefaultAudioFrameSenderData.provider] and
     * sends them to Discord.
     */
    public suspend fun start(configuration: AudioFrameSenderConfiguration)
}
