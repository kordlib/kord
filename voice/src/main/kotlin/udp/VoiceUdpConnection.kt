package dev.kord.voice.udp

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val udpConnectionLogger = KotlinLogging.logger { }

internal class VoiceUdpConnection(
    override val server: NetworkAddress,
    private val ssrc: Int,
    dispatcher: CoroutineDispatcher
) : CoroutineScope, DiscordUdpConnection {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Discord Voice UDP Connection")

    private val socket = aSocket(ActorSelectorManager(coroutineContext)).udp().connect(server) // udp connection
    override val isOpen: Boolean get() = !socket.isClosed

    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalIoApi::class)
    internal suspend fun performIpDiscovery(): NetworkAddress {
        udpConnectionLogger.trace { "discovering ip" }

        sendPacket {
            writeInt(ssrc)
            writeFully(ByteArray(66))
        }

        receivePacket().also {
            it.discardExact(4)
            val ip = it.readTextExact(64)
                .trimEnd { it.code == 0 } // response string is null terminated and padded with nulls
            val port = it.readUShort().toInt()

            return NetworkAddress(ip, port)
        }
    }

    override suspend fun send(packet: Datagram) {
        socket.outgoing.send(packet)
    }

    override suspend fun receive(): Datagram {
        return socket.incoming.consumeAsFlow().first()
    }

    override suspend fun close() {
        socket.close()
    }
}