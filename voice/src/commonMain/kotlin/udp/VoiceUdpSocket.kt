package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.io.ByteArrayView
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.io.Source

@KordVoice
public typealias SocketAddress = InetSocketAddress

/**
 * A global [VoiceUdpSocket] for all [dev.kord.voice.VoiceConnection]s, unless specified otherwise.
 * Initiated once and kept open for the lifetime of this process.
 */
@KordVoice
public val GlobalVoiceUdpSocket: VoiceUdpSocket = object : VoiceUdpSocket {
    private val socketScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("kord-voice-global-socket"))

    private val socket = socketScope.async {
        aSocket(SelectorManager(socketScope.coroutineContext)).udp().bind()
    }

    private val incoming: MutableSharedFlow<Datagram> = MutableSharedFlow()

    init {
        socketScope.launch { incoming.emitAll(socket.await().incoming) }
    }

    override fun all(address: SocketAddress): Flow<Source> {
        return incoming
            .filter { it.address == address }
            .map { it.packet }
    }

    override suspend fun send(address: SocketAddress, packet: ByteArrayView) {
        val brp = ByteReadPacket(packet.data, packet.dataStart, packet.viewSize)
        socket.await().send(Datagram(brp, address))
    }

    override suspend fun stop() {
    }
}

@KordVoice
public interface VoiceUdpSocket {
    public fun all(address: SocketAddress): Flow<Source>

    public suspend fun send(address: SocketAddress, packet: ByteArrayView)

    public suspend fun stop()

    public companion object {
        private object None : VoiceUdpSocket {
            override fun all(address: SocketAddress): Flow<Source> = emptyFlow()

            override suspend fun send(address: SocketAddress, packet: ByteArrayView) {}

            override suspend fun stop() {}
        }

        public fun none(): VoiceUdpSocket = None
    }
}

public suspend fun VoiceUdpSocket.recv(address: SocketAddress): Source = all(address).first()
