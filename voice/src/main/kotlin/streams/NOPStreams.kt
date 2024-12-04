package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.EncryptionMode
import dev.kord.voice.XSalsa20_FUNCTION_DEPRECATION
import dev.kord.voice.udp.DecryptedVoicePacket
import dev.kord.voice.udp.RTPPacket
import io.ktor.network.sockets.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@KordVoice
public object NOPStreams : Streams {
    @Deprecated(
        "This functions always uses XSalsa20 Poly1305 encryption. Pass an explicit 'EncryptionMode' instead. " +
            XSalsa20_FUNCTION_DEPRECATION,
        ReplaceWith(
            "this.listen(key, server, EncryptionMode.AeadXChaCha20Poly1305RtpSize)",
            imports = ["dev.kord.voice.EncryptionMode"],
        ),
        DeprecationLevel.WARNING,
    )
    override suspend fun listen(key: ByteArray, server: SocketAddress) {}

    override suspend fun listen(key: ByteArray, server: SocketAddress, encryptionMode: EncryptionMode) {}

    override val incomingAudioPackets: Flow<RTPPacket> = flow { }
    override val incomingVoicePackets: Flow<DecryptedVoicePacket> = flow { }
    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>> = flow { }
    override val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>> = flow { }
    override val ssrcToUser: Map<UInt, Snowflake> = emptyMap()
}
