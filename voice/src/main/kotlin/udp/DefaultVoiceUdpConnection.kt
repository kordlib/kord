package dev.kord.voice.udp

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val udpConnectionLogger = KotlinLogging.logger { }

data class DefaultVoiceUdpConnectionData(
    val dispatcher: CoroutineDispatcher
)

internal class DefaultVoiceUdpConnection(
    val data: DefaultVoiceUdpConnectionData
) : VoiceUdpConnection, CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + data.dispatcher + CoroutineName("Discord Voice UDP Connection")

    private lateinit var socket: ConnectedDatagramSocket
    private lateinit var configuration: VoiceUdpConnectionConfiguration

    private val _incoming: MutableSharedFlow<ByteReadPacket> = MutableSharedFlow()
    override val incoming: SharedFlow<ByteReadPacket> = _incoming

    override suspend fun start(configuration: VoiceUdpConnectionConfiguration) {
        this.configuration = configuration
        if (::socket.isInitialized) withContext(Dispatchers.IO) { socket.close() }

        socket = aSocket(ActorSelectorManager(coroutineContext)).udp().connect(configuration.server)

        (this@DefaultVoiceUdpConnection).launch {
            _incoming.emitAll(socket.incoming.receiveAsFlow().map { it.packet })
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun discoverIp(): NetworkAddress {
        udpConnectionLogger.trace { "discovering ip" }

        send(buildPacket {
            writeUInt(configuration.ssrc)
            writeFully(ByteArray(66))
        })

        with(receive()) {
            discardExact(4)
            val ip = readTextExact(64).trimEnd { it.code == 0 } // response string is null terminated and padded with nulls
            val port = readUShort().toInt()

            return NetworkAddress(ip, port)
        }
    }

    override suspend fun send(data: ByteReadPacket) {
        socket.outgoing.send(Datagram(data, socket.remoteAddress))
    }

    private suspend fun receive(): ByteReadPacket {
        return incoming.first()
    }
}