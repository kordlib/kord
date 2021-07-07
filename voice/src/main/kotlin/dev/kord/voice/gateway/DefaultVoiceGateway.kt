package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.*
import dev.kord.voice.command.*
import dev.kord.voice.event.HelloVoiceEvent
import dev.kord.voice.event.ReadyVoiceEvent
import dev.kord.voice.event.SessionDescriptionEvent
import dev.kord.voice.event.VoiceEvent
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import me.uport.knacl.nacl
import mu.KotlinLogging
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext

private val defaultVoiceGatewayLogger = KotlinLogging.logger {}

data class VoiceOptions(
    val guildId: Snowflake,
    val channelId: Snowflake,
    val selfMute: Boolean = false,
    val selfDeaf: Boolean = false
)

@ObsoleteCoroutinesApi
class DefaultVoiceGateway(val gateway: Gateway, val client: HttpClient, val voiceOpitons: VoiceOptions) : VoiceGateway {

    private lateinit var udp: ConnectedDatagramSocket

    private lateinit var socket: DefaultClientWebSocketSession


    private val ticker = Ticker()

    val _events = MutableSharedFlow<VoiceEvent>()
    override val events: SharedFlow<VoiceEvent> = _events

    private val sequence = atomic<Short>(0)

    internal var ssrc: Int? = null

    private lateinit var secretKey: ByteArray


    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val rtpHeader
        get() = ByteBuffer.allocate(12)
            .put(0x80.toByte())
            .put(0x78)
            .putShort(sequence.getAndUpdate { (it + 1).toShort() })
            .putInt(sequence.value * 960)
            .putInt(ssrc!!)
            .array()


    override suspend fun connect() {

        val (voiceStateUpdateVoice, serverUpdateEvent) = retrieveVoiceServerInformation()

        heartbeat()

        val voiceReadyEvent = establishConnection(voiceStateUpdateVoice, serverUpdateEvent)


        val ssrc = voiceReadyEvent.ready.ssrc
        this.ssrc = ssrc
        val network = NetworkAddress(voiceReadyEvent.ready.ip, voiceReadyEvent.ready.port)
        udp = aSocket(ActorSelectorManager(Dispatchers.IO)).udp().connect(network)

        val ip = ipDiscovery(network)

        println(network)

        println("is closed: ${udp.isClosed}")

        send(
            VoiceSelectProtocolCommand(
                protocol = "udp",
                data = VoiceSelectProtocolCommandData(ip.hostname, ip.port, "xsalsa20_poly1305")
            )
        )

        val sesFrame = events.filterIsInstance<SessionDescriptionEvent>().first()

        println("set key")
        secretKey = sesFrame.sessionDescription.secretKey.toUByteArray().toByteArray()
    }

    override suspend fun resume() {
        TODO("Not implemented yet")
    }


    private suspend fun trySend(command: VoiceCommand): Boolean {
        return try {
            sendUnsafe(command)
            true
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private suspend fun sendUnsafe(command: VoiceCommand) {
        val json = Json.encodeToString(VoiceCommand, command)
        defaultVoiceGatewayLogger.trace { "Gateway >>> $json" }
        socket.send(json)
        println("SENDING: $json")
    }


    override suspend fun send(command: VoiceCommand): Boolean {
        return trySend(command)
    }


    override suspend fun disconnect() {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Default

    private suspend fun sendUpdateVoiceStatus(voiceOpitons: VoiceOptions) {
        gateway.send(
            UpdateVoiceStatus(
                guildId = voiceOpitons.guildId,
                channelId = voiceOpitons.channelId,
                selfDeaf = voiceOpitons.selfDeaf,
                selfMute = voiceOpitons.selfMute
            )
        )
    }

    private suspend fun retrieveVoiceServerInformation(): Pair<VoiceStateUpdate, VoiceServerUpdate> {
        val voiceStateUpdateEventWaiter: Deferred<VoiceStateUpdate> = waitForTCPGatewayAsync()
        val serverUpdateEventWaiter: Deferred<VoiceServerUpdate> = waitForTCPGatewayAsync()

        sendUpdateVoiceStatus(voiceOpitons)

        val voiceStateUpdateVoice = voiceStateUpdateEventWaiter.await()
        val serverUpdateEvent = serverUpdateEventWaiter.await()

        return Pair(voiceStateUpdateVoice, serverUpdateEvent)
    }

    private suspend fun webSocket(endpoint: String): DefaultClientWebSocketSession {
        return client.webSocketSession { url("wss://$endpoint") }

    }

    private suspend fun ipDiscovery(address: NetworkAddress): NetworkAddress {
        val buffer = BytePacketBuilder().also {
            it.writeInt(ssrc!!)
            it.writeFully(ByteArray(66))
        }
        val datagram = Datagram(buffer.build(), address)
        udp.send(datagram)
        val received = udp.incoming.receive().packet
        received.discardExact(4)
        val ip = received.readBytes(64).decodeToString().trimEnd { it.code == 0 }
        val port = received.readUShort().toInt()

        return NetworkAddress(ip, port)
    }

    private suspend fun establishConnection(
        voiceState: VoiceStateUpdate,
        voiceServerUpdate: VoiceServerUpdate
    ): ReadyVoiceEvent {
        val endpoint = voiceServerUpdate.voiceServerUpdateData.endpoint + "?v=4" ?: error("No endpoint recieved.")
        socket = webSocket(endpoint)
        launch {
            readSocket()
        }
        val readyEventWaiter = waitFor<ReadyVoiceEvent>()
        send(
            VoiceIdentifyCommand(
                voiceState.voiceState.guildId.value!!.asString,
                voiceState.voiceState.userId.asString,
                voiceState.voiceState.sessionId,
                voiceServerUpdate.voiceServerUpdateData.token

            )
        )
        return readyEventWaiter.await()
    }


    private suspend fun heartbeat() {
        on<HelloVoiceEvent> {
            ticker.tickAt(hello.heartHeatInterval.toLong()) {
                send(VoiceHeartbeatCommand(Clock.System.now().epochSeconds))
            }
        }
    }

    override suspend fun sendEncryptedVoice(data: ByteArray) {
        val currentRtpHeader = rtpHeader
        val nonce = currentRtpHeader.copyOf(24)
        val encrypted = nacl.secretbox.seal(data, nonce, secretKey)
        defaultVoiceGatewayLogger.trace { "Gateway >>> packet" }

        udp.send(Datagram(ByteReadPacket(currentRtpHeader + encrypted), udp.remoteAddress))
        println("sended")
    }

    private suspend inline fun <reified T> waitForTCPGatewayAsync(): Deferred<T> = async {
        gateway.events.filterIsInstance<T>().take(1).first()
    }


    private suspend inline fun <reified T> waitFor(): Deferred<T> = async {
        events.filterIsInstance<T>().take(1).first()
    }

    init {
        launch {
            events.collect {
                println("INCOMING: $it")
            }

        }
    }


    private suspend fun readSocket() {
        socket.incoming.receiveAsFlow().buffer(Channel.UNLIMITED).collect {
            when (it) {
                is Frame.Binary, is Frame.Text -> read(it)
                else -> { /*ignore*/
                }
            }
        }

    }

    private suspend fun read(frame: Frame) {
        val json =  String(frame.data, Charsets.UTF_8)

        try {
            println("RECEIVED RAW: $json")
            defaultVoiceGatewayLogger.trace { "Gateway <<< $json" }
            val event = jsonParser.decodeFromString(VoiceEvent.FrameSerializer, json)
            _events.emit(event)
            println("EMITTED")
        } catch (exception: Exception) {
            println("oopsie")
            exception.printStackTrace()
            defaultVoiceGatewayLogger.error(exception)
        }

    }


}