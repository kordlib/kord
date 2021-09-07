package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.SharedFlow

@KordVoice
interface VoiceUdpConnection {
    val incoming: SharedFlow<ByteReadPacket>

    suspend fun discoverIp(): NetworkAddress

    suspend fun send(data: ByteReadPacket)

    suspend fun start(configuration: VoiceUdpConnectionConfiguration)
}

@KordVoice
data class VoiceUdpConnectionConfiguration(
    val server: NetworkAddress,
    val ssrc: UInt
)