@file:OptIn(KordVoice::class)

package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.gateway.VoiceServerUpdate
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
            if (!voiceServerUpdate.isRelatedToConnection(connection)) return@on

            voiceUpdateLogger.trace { "changing voice servers for session ${connection.data.sessionId}" }

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

private fun VoiceServerUpdate.isRelatedToConnection(connection: VoiceConnection): Boolean {
    return voiceServerUpdateData.guildId != connection.data.guildId
}