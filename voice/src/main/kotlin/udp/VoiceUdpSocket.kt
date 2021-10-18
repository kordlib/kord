package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@KordVoice
interface VoiceUdpSocket {
    val incoming: SharedFlow<Datagram>

    suspend fun discoverIp(address: NetworkAddress, ssrc: Int): NetworkAddress

    suspend fun send(packet: Datagram)

    suspend fun stop()

    companion object {
        private object None : VoiceUdpSocket {
            override val incoming: SharedFlow<Datagram> = MutableSharedFlow()

            override suspend fun discoverIp(address: NetworkAddress, ssrc: Int): NetworkAddress {
                return address
            }

            override suspend fun send(packet: Datagram) {}

            override suspend fun stop() {}
        }

        fun none(): VoiceUdpSocket = None
    }
}

@KordVoice
suspend fun VoiceUdpSocket.receiveFrom(address: NetworkAddress) = incoming.filter { it.address == address }.first()