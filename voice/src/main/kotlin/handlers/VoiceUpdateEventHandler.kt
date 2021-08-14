package dev.kord.voice.handlers

import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.kord.voice.VoiceConnection
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import dev.kord.gateway.Event as GatewayEvent

private val voiceUpdateLogger = KotlinLogging.logger { }

internal class VoiceUpdateEventHandler(
    private val connection: VoiceConnection,
    flow: Flow<GatewayEvent>,
) : EventHandler<GatewayEvent>(flow, "VoiceUpdateInterceptor") {
    private var configuration = connection.voiceGatewayConfiguration

    override fun start() {
        on<VoiceStateUpdate> { voiceState ->
            // see if this voice state update interests this voice connection
            if (connection.data.guildId != voiceState.voiceState.guildId.value) return@on
            if (connection.data.selfId != voiceState.voiceState.userId) return@on

            // check if were moved into nothing, aka disconnected, or maybe the channel was deleted...
            if (voiceState.voiceState.channelId == null) {
                connection.disconnect()
                return@on
            }

            // update the gateway configuration using what we know
            configuration = configuration.copy(
                channelId = voiceState.voiceState.channelId!!,
                sessionId = voiceState.voiceState.sessionId,
            )

            // now we should wait for a voice server update...
        }

        on<VoiceServerUpdate> { voiceServerUpdate ->
            // see if this voice server update interests this voice connection
            if (voiceServerUpdate.voiceServerUpdateData.guildId != connection.data.guildId) return@on

            // update the gateway configuration accordingly
            connection.voiceGatewayConfiguration = connection.voiceGatewayConfiguration.copy(
                token = voiceServerUpdate.voiceServerUpdateData.token,
                endpoint = "wss://${voiceServerUpdate.voiceServerUpdateData.endpoint}?v=4"
            )

            // reconnect...
            connection.disconnect()
            connection.connect()
        }
    }
}