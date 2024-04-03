package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.io.ByteArrayView
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.*

@KordVoice
public expect class SocketAddress(hostname: String, port: Int) {
    public val hostname: String

    public val port: Int
}

/**
 * A global [VoiceUdpSocket] for all [dev.kord.voice.VoiceConnection]s, unless specified otherwise.
 * Initiated once and kept open for the lifetime of this process.
 */
@KordVoice
public expect val GlobalVoiceUdpSocket: VoiceUdpSocket

@KordVoice
public interface VoiceUdpSocket {
    public fun all(address: SocketAddress): Flow<ByteReadPacket>

    public suspend fun send(address: SocketAddress, packet: ByteArrayView): Unit

    public suspend fun stop()

    public companion object {
        private object None : VoiceUdpSocket {
            override fun all(address: SocketAddress): Flow<ByteReadPacket> = emptyFlow()

            override suspend fun send(address: SocketAddress, packet: ByteArrayView) {}

            override suspend fun stop() {}
        }

        public fun none(): VoiceUdpSocket = None
    }
}

public suspend fun VoiceUdpSocket.recv(address: SocketAddress): ByteReadPacket = all(address).first()
