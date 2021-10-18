package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@KordVoice
interface VoiceUdpSocket {
    val incoming: SharedFlow<Datagram>

    suspend fun discoverIp(address: NetworkAddress, ssrc: Int): NetworkAddress

    suspend fun send(packet: Datagram)

    suspend fun receive(): Datagram = incoming.first()

    suspend fun stop()
}

@KordVoice
suspend fun VoiceUdpSocket.receiveFrom(address: NetworkAddress) = incoming.filter { it.address == address }.first()