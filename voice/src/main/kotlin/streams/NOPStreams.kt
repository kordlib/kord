package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.udp.RTPPacket
import io.ktor.util.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@KordVoice
public object NOPStreams : Streams {
    override suspend fun listen(key: ByteArray, server: NetworkAddress) {}

    override val incomingAudioPackets: Flow<RTPPacket> = flow { }
    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>> = flow { }
    override val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>> = flow { }
    override val ssrcToUser: Map<UInt, Snowflake> = emptyMap()
}