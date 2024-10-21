package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.io.ByteArrayView
import io.ktor.network.sockets.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.Datagram
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.io.Source
import io.ktor.network.sockets.Datagram as KtorDatagram

@KordVoice
public actual typealias SocketAddress = InetSocketAddress

@KordVoice
public actual val GlobalVoiceUdpSocket: VoiceUdpSocket = object : VoiceUdpSocket {
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
        socket.await().send(KtorDatagram(brp, address))
    }

    override suspend fun stop() {
    }
}

