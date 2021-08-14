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
    override fun start() {
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