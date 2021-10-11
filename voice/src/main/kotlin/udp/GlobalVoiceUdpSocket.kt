package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.text.String

private val globalVoiceSocketLogger = KotlinLogging.logger { }

/**
 * A global [VoiceUdpSocket] for all [dev.kord.voice.VoiceConnection]s, unless specified otherwise.
 * Usually initiated on the first connection, and will stay "open" until [stop] is called.
 */
@KordVoice
object GlobalVoiceUdpSocket : VoiceUdpSocket {
    private val socketScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _incoming: MutableSharedFlow<Datagram> = MutableSharedFlow()
    override val incoming: SharedFlow<Datagram> = _incoming

    // once we discover it, it will stay the same for the rest of this socket's lifetime.
    private var ourAddress: NetworkAddress? = null

    private val socket = aSocket(ActorSelectorManager(socketScope.coroutineContext)).udp().bind()

    init {
        socket.incoming
            .consumeAsFlow()
            .onEach { _incoming.emit(it) }
            .launchIn(socketScope)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun discoverIp(address: NetworkAddress, ssrc: Int): NetworkAddress {
        globalVoiceSocketLogger.trace { "discovering ip" }

        if (ourAddress != null) return ourAddress!!

        send(packet(address) {
            writeInt(ssrc)
            writeFully(ByteArray(66))
        })

        with(receiveFrom(address).packet) {
            discard(4)
            val ip = String(readBytes(64)).trimEnd(0.toChar())
            val port = readUShort().toInt()

            ourAddress = NetworkAddress(ip, port)
        }

        return ourAddress!!
    }

    override suspend fun send(packet: Datagram) {
        socket.send(packet)
    }

    override suspend fun stop() {
        socketScope.cancel()
    }

    private fun packet(address: NetworkAddress, builder: BytePacketBuilder.() -> Unit): Datagram {
        return Datagram(buildPacket(block = builder), address)
    }
}