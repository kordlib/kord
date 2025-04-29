@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.EncryptionMode
import dev.kord.voice.FrameInterceptorConfiguration
import dev.kord.voice.XSalsa20_CONSTRUCTOR_DEPRECATION
import io.ktor.network.sockets.*

@KordVoice
public data class AudioFrameSenderConfiguration(
    val server: SocketAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val interceptorConfiguration: FrameInterceptorConfiguration,
    val encryptionMode: EncryptionMode,
) {
    @Deprecated(
        "An 'AudioFrameSenderConfiguration' instance must be created with an 'encryptionMode'. " +
            XSalsa20_CONSTRUCTOR_DEPRECATION,
        ReplaceWith(
            "AudioFrameSenderConfiguration(server, ssrc, key, interceptorConfiguration, " +
                "EncryptionMode.AeadXChaCha20Poly1305RtpSize)",
            imports = ["dev.kord.voice.udp.AudioFrameSenderConfiguration", "dev.kord.voice.EncryptionMode"],
        ),
        DeprecationLevel.WARNING,
    )
    public constructor(
        server: SocketAddress, ssrc: UInt, key: ByteArray, interceptorConfiguration: FrameInterceptorConfiguration,
    ) : this(
        server, ssrc, key, interceptorConfiguration,
        EncryptionMode.from("AudioFrameSenderConfiguration.encryptionMode placeholder"),
    )

    @Deprecated(
        "Kept for binary compatibility, this function will be removed in 0.19.0.",
        level = DeprecationLevel.HIDDEN,
    )
    public fun copy(
        server: SocketAddress = this.server, ssrc: UInt = this.ssrc, key: ByteArray = this.key,
        interceptorConfiguration: FrameInterceptorConfiguration = this.interceptorConfiguration,
    ): AudioFrameSenderConfiguration =
        AudioFrameSenderConfiguration(server, ssrc, key, interceptorConfiguration, encryptionMode)
}

@KordVoice
public interface AudioFrameSender {
    /**
     * This should start polling frames from [the audio provider][DefaultAudioFrameSenderData.provider] and
     * send them to Discord.
     */
    public suspend fun start(configuration: AudioFrameSenderConfiguration)
}
