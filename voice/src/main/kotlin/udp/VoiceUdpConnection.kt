package dev.kord.voice.udp

import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.SharedFlow

interface VoiceUdpConnection {
    val incoming: SharedFlow<ByteReadPacket>

    suspend fun discoverIp(): NetworkAddress

    suspend fun send(data: ByteReadPacket)

    suspend fun start(configuration: VoiceUdpConnectionConfiguration)
}

data class VoiceUdpConnectionConfiguration(
    val server: NetworkAddress,
    val ssrc: UInt
)