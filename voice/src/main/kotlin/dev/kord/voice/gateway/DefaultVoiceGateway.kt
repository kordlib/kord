package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.*
import dev.kord.voice.command.*
import dev.kord.voice.event.HelloVoiceEvent
import dev.kord.voice.event.ReadyVoiceEvent
import dev.kord.voice.event.SessionDescription
import dev.kord.voice.event.VoiceEvent
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.datetime.Clock
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext

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

    override val events: Flow<VoiceEvent>
        get() = TODO("Not yet implemented")

    override suspend fun connect() {

        val (voiceStateUpdateVoice, serverUpdateEvent) = retrieveVoiceServerInformation()

        val voiceReadyEvent = establishConnection(voiceStateUpdateVoice, serverUpdateEvent)

        heartbeat()

        val externalNetwork = ipDiscovery(
            voiceReadyEvent.ssrc,
            NetworkAddress(voiceReadyEvent.ip, voiceReadyEvent.port)
        )

        val sessionDescriptionWaiter: Deferred<SessionDescription> = waitFor()

        send(
            VoiceSelectProtocolCommand(
                "udp",
                VoiceSelectProtocolCommandData(externalNetwork.hostname, externalNetwork.port, "xsalsa20_poly1305")
            )
        )

        val sessionDescription = sessionDescriptionWaiter.await()

    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun send(command: VoiceCommand) {
        TODO("Not yet implemented")
    }


    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

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

    private suspend fun ipDiscovery(ssrc: Int, address: NetworkAddress): NetworkAddress {
        val buffer = ByteBuffer.allocate(70)
            .putShort(1)
            .putShort(70)
            .putInt(ssrc)
        val datagram = Datagram(ByteReadPacket(buffer), address)
        udp.send(datagram)
        val received = udp.receive().packet
        received.discardExact(4)
        val ip = received.readBytes(received.remaining.toInt() - 2).toString().trim()
        val port = received.readShortLittleEndian().toInt()

        return NetworkAddress(ip, port)
    }

    private suspend fun establishConnection(
        voiceState: VoiceStateUpdate,
        voiceServerUpdate: VoiceServerUpdate
    ): ReadyVoiceEvent {
        val endpoint = voiceServerUpdate.voiceServerUpdateData.endpoint ?: error("No endpoint recieved.")
        socket = webSocket(endpoint)
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
            ticker.tickAt(heartbeatInterval) {
                send(VoiceHeartbeatCommand(Clock.System.now().epochSeconds))
            }
        }

        TODO("Ping")
    }

    private suspend inline fun <reified T> waitForTCPGatewayAsync(): Deferred<T> = async {
        gateway.events.filterIsInstance<T>().take(1).first()
    }


    private suspend inline fun <reified T> waitFor(): Deferred<T> = async {
        events.filterIsInstance<T>().take(1).first()
    }


}