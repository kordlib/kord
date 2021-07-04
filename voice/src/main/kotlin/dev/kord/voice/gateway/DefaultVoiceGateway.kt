package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.kord.voice.command.VoiceCommand
import dev.kord.voice.command.VoiceIdentifyCommand
import dev.kord.voice.event.ReadyVoiceEvent
import dev.kord.voice.event.VoiceEvent
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlin.coroutines.CoroutineContext

data class VoiceOptions(
    val guildId: Snowflake,
    val channelId: Snowflake,
    val selfMute: Boolean = false,
    val selfDeaf: Boolean = false
)

class DefaultVoiceGateway(val gateway: Gateway, val client: HttpClient, val voiceOpitons: VoiceOptions) : VoiceGateway {
    private lateinit var socket: DefaultClientWebSocketSession
    override val events: Flow<VoiceEvent>
        get() = TODO("Not yet implemented")

    override suspend fun connect() {
        val voiceStateUpdateEventWaiter: Deferred<VoiceStateUpdate> = waitForTCPGatewayAsync()
        val serverUpdateEventWaiter: Deferred<VoiceServerUpdate> = waitForTCPGatewayAsync()

        sendUpdateVoiceStatus(voiceOpitons)

        val voiceStateUpdateVoice = voiceStateUpdateEventWaiter.await()
        val serverUpdateEvent = serverUpdateEventWaiter.await()

        establishConnection(voiceStateUpdateVoice, serverUpdateEvent)

        heartbeat()


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

    private suspend fun webSocket(endpoint: String): DefaultClientWebSocketSession {
        return client.webSocketSession { url("wss://$endpoint") }

    }

    private suspend fun establishConnection(voiceState: VoiceStateUpdate, voiceServerUpdate: VoiceServerUpdate): ReadyVoiceEvent {
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


    private fun heartbeat() {
        TODO("Not yet implemented")
    }
    private suspend inline fun <reified T> waitForTCPGatewayAsync(): Deferred<T> = coroutineScope {
        async {
            gateway.events.filterIsInstance<T>().take(1).first()
        }
    }


    private suspend inline fun <reified T> waitFor(): Deferred<T> = coroutineScope {
        async {
            events.filterIsInstance<T>().take(1).first()
        }
    }


}