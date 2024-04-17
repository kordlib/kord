package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.io.ByteArrayView
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow

@KordVoice
public actual val GlobalVoiceUdpSocket: VoiceUdpSocket = object : VoiceUdpSocket {
    override fun all(address: SocketAddress): Flow<ByteReadPacket> = unsupported()

    override suspend fun send(address: SocketAddress, packet: ByteArrayView) = unsupported()

    override suspend fun stop() = unsupported()
}

// https://youtrack.jetbrains.com/issue/KTOR-4080
private fun unsupported(): Nothing = TODO("Voice is not supported on windows")
