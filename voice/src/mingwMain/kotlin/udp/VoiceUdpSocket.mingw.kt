package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.io.ByteArrayView
import kotlinx.coroutines.flow.Flow
import kotlinx.io.Source

@KordVoice
public actual val GlobalVoiceUdpSocket: VoiceUdpSocket = object : VoiceUdpSocket {
    override fun all(address: SocketAddress): Flow<Source> = unsupported()

    override suspend fun send(address: SocketAddress, packet: ByteArrayView) = unsupported()

    override suspend fun stop() = unsupported()
}

// https://youtrack.jetbrains.com/issue/KTOR-4080
private fun unsupported(): Nothing = TODO("Voice is not supported on windows")
