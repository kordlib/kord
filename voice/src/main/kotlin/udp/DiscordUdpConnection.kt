package dev.kord.voice.udp

import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*

internal interface DiscordUdpConnection {
    val server: NetworkAddress

    val isOpen: Boolean

    suspend fun send(packet: Datagram)

    suspend fun receive(): Datagram

    suspend fun close()
}

internal fun DiscordUdpConnection.packet(packetBuilder: BytePacketBuilder.() -> Unit) =
    Datagram(BytePacketBuilder().also(packetBuilder).build(), server)

internal suspend fun DiscordUdpConnection.receivePacket() = receive().packet

internal suspend fun DiscordUdpConnection.sendPacket(builder: BytePacketBuilder.() -> Unit) = send(packet(builder))