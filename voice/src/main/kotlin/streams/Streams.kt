package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.udp.AudioPacket
import kotlinx.coroutines.flow.Flow


/**
 * A representation of receiving voice through Discord and different stages of processing.
 */
@KordVoice
interface Streams {
    /**
     * An encryption key used for decryption of Discord packets.
     */
    var key: ByteArray?

    /**
     * A flow of all incoming [dev.kord.voice.udp.AudioPacket.DecryptedPacket]s through the UDP connection.
     */
    val incomingAudioPackets: Flow<AudioPacket.DecryptedPacket>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [ssrc][UInt].
     */
    val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [userId][Snowflake].
     * Streams for every user should be built over time and will not be immediately available.
     */
    val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>>

    /**
     * A map of [ssrc][UInt]s to their corresponding [userId][Snowflake].
     */
    val ssrcToUser: Map<UInt, Snowflake>
}