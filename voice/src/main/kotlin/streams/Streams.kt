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

/**
 * A representation of receiving voice through Discord and different stages of processing.
 */
@KordVoice
public interface Streams {
    /**
     * Starts propagating packets from [server] with the following [key] to decrypt the incoming frames.
     */
    @Deprecated(
        "This functions always uses XSalsa20 Poly1305 encryption. Pass an explicit 'EncryptionMode' instead. " +
            XSalsa20_FUNCTION_DEPRECATION,
        ReplaceWith(
            "this.listen(key, server, EncryptionMode.AeadXChaCha20Poly1305RtpSize)",
            imports = ["dev.kord.voice.EncryptionMode"],
        ),
        DeprecationLevel.WARNING,
    )
    public suspend fun listen(key: ByteArray, server: SocketAddress)

    /**
     * Starts propagating packets from [server] with the following [key] to decrypt the incoming frames according to
     * [encryptionMode].
     */
    public suspend fun listen(key: ByteArray, server: SocketAddress, encryptionMode: EncryptionMode)

    /**
     * A flow of all incoming [dev.kord.voice.udp.RTPPacket]s through the UDP connection.
     */
    public val incomingAudioPackets: Flow<RTPPacket>

    /**
     * A flow of all incoming [DecryptedVoicePacket]s through the UDP connection.
     */
    public val incomingVoicePackets: Flow<DecryptedVoicePacket>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [ssrc][UInt].
     */
    public val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [userId][Snowflake].
     * Streams for every user should be built over time and will not be immediately available.
     */
    public val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>>

    /**
     * A map of [ssrc][UInt]s to their corresponding [userId][Snowflake].
     */
    public val ssrcToUser: Map<UInt, Snowflake>
}
