package dev.kord.voice.handlers

import dev.kord.common.KordConfiguration
import dev.kord.common.annotation.KordVoice
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.kord.voice.VoiceConnection
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.time.Duration
import dev.kord.gateway.Event as GatewayEvent

private val voiceUpdateLogger = KotlinLogging.logger { }

@KordVoice
internal class VoiceUpdateEventHandler(
    flow: Flow<GatewayEvent>,
    private val connectionDetachDuration: Duration,
    private val connection: VoiceConnection,
) : ConnectionEventHandler<GatewayEvent>(flow, "VoiceUpdateInterceptor") {
    private var detachJob: Job? by atomic(null)

    override suspend fun start() = coroutineScope {
        on<VoiceStateUpdate> { event ->
            if (!event.isRelatedToConnection(connection)) return@on

            // we're not in a voice channel anymore. anything might've happened
            // discord doesn't tell us whether the channel was deleted or if we were just moved
            // let's just detach in a specified duration and let it be cancelled if we join a new voice channel in that time.
            detachJob = if (event.voiceState.channelId == null) {
                voiceUpdateLogger.trace { "detected a change to a null voice channel for guild ${connection.data.guildId}. waiting $connectionDetachDuration before shutdown to see if we were moved." }

                detachJob?.cancel()
                launch {
                    delay(connectionDetachDuration)

                    connection.shutdown()
                }
            } else {
                voiceUpdateLogger.trace { "detected a voice channel change for guild ${connection.data.guildId}, cancelling detachment." }
                detachJob?.cancel()
                null
            }
        }

        on<VoiceServerUpdate> { voiceServerUpdate ->
            if (!voiceServerUpdate.isRelatedToConnection(connection)) return@on

            // voice server has gone away.
            if (voiceServerUpdate.voiceServerUpdateData.endpoint == null) {
                connection.disconnect()
            }

            voiceUpdateLogger.trace { "changing voice servers for session ${connection.data.sessionId}" }

            // update the gateway configuration accordingly
            connection.voiceGatewayConfiguration = connection.voiceGatewayConfiguration.copy(
                token = voiceServerUpdate.voiceServerUpdateData.token,
                endpoint = "wss://${voiceServerUpdate.voiceServerUpdateData.endpoint}/?v=${KordConfiguration.VOICE_GATEWAY_VERSION}",
            )

            // reconnect...
            connection.disconnect()
            connection.connect()
        }
    }
}

@KordVoice
private fun VoiceServerUpdate.isRelatedToConnection(connection: VoiceConnection): Boolean {
    return voiceServerUpdateData.guildId == connection.data.guildId
}

@KordVoice
private fun VoiceStateUpdate.isRelatedToConnection(connection: VoiceConnection): Boolean {
    return voiceState.guildId.value == connection.data.guildId && voiceState.userId == connection.data.selfId
}
