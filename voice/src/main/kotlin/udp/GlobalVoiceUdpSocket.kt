package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.text.String

private val globalVoiceSocketLogger = KotlinLogging.logger { }

/**
 * A global [VoiceUdpSocket] for all [dev.kord.voice.VoiceConnection]s, unless specified otherwise.
 * Initiated once and kept open for the lifetime of this process.
 */
@KordVoice
public object GlobalVoiceUdpSocket : VoiceUdpSocket {
    private val socketScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("kord-voice-global-socket"))

    private val _incoming: MutableSharedFlow<Datagram> = MutableSharedFlow()
    override val incoming: SharedFlow<Datagram> = _incoming

    private val socket = aSocket(ActorSelectorManager(socketScope.coroutineContext)).udp().bind()

    init {
        socket.incoming
            .consumeAsFlow()
            .onEach { _incoming.emit(it) }
            .launchIn(socketScope)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun discoverIp(address: InetSocketAddress, ssrc: Int): InetSocketAddress {
        globalVoiceSocketLogger.trace { "discovering ip" }

        send(packet(address) {
            writeInt(ssrc)
            writeFully(ByteArray(66))
        })

        return with(receiveFrom(address).packet) {
            discard(4)
            val ip = String(readBytes(64)).trimEnd(0.toChar())
            val port = readUShort().toInt()

            InetSocketAddress(ip, port)
        }
    }

    override suspend fun send(packet: Datagram) {
        socket.send(packet)
    }

    override suspend fun stop() { /* this doesn't stop until the end of the process */ }

    private fun packet(address: SocketAddress, builder: BytePacketBuilder.() -> Unit): Datagram {
        return Datagram(buildPacket(block = builder), address)
    }
}
