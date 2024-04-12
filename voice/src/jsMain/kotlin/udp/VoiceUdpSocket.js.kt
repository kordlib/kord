package dev.kord.voice.udp

import dev.kord.voice.io.ByteArrayView
import io.ktor.utils.io.core.*
import js.typedarrays.toUint8Array
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import node.dgram.SocketEvent
import node.dgram.SocketType
import node.dgram.createSocket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public actual val GlobalVoiceUdpSocket: VoiceUdpSocket = object : VoiceUdpSocket {
    private val socketScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("kord-voice-global-socket"))

    private val incoming = MutableSharedFlow<Pair<SocketAddress, ByteArray>>()

    private val socket = createSocket(SocketType.udp4)

    init {
        socket.on(SocketEvent.MESSAGE) { message, info ->
            //
            socketScope.launch {
                incoming.emit(SocketAddress(info.address, info.port.toInt()) to message.toByteArray())
            }
        }
    }

    override fun all(address: SocketAddress): Flow<ByteReadPacket> = incoming
        .filter { it.first == address }
        .map { ByteReadPacket(it.second) }

    override suspend fun send(address: SocketAddress, packet: ByteArrayView) {
        suspendCoroutine { cont ->
            socket.send(
                packet.data.toUint8Array(),
                packet.dataStart,
                packet.viewSize,
                address.port,
                address.hostname
            ) { error, _ ->
                if (error != null) {
                    cont.resumeWithException(error)
                } else {
                    cont.resume(Unit)
                }
            }
        }
    }

    override suspend fun stop() {
    }
}
