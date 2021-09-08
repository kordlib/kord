package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.udp.AudioPacket
import kotlinx.coroutines.flow.Flow

@KordVoice
object NOPStreams : Streams {
    override var key: ByteArray? = null

    override val incomingAudioPackets: Flow<AudioPacket.DecryptedPacket>
        get() = nopStreamsException()
    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>
        get() = nopStreamsException()
    override val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>>
        get() = nopStreamsException()
    override val ssrcToUser: Map<UInt, Snowflake>
        get() = nopStreamsException()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun nopStreamsException(): Nothing =
        throw NotImplementedError("NOP implementation being used, try to enable voice receiving.")
}