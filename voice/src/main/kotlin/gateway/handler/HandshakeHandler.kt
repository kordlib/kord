package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

private const val SUPPORTED_DAVE_PROTOCOL_VERSION = 0

internal class HandshakeHandler(
    flow: Flow<VoiceEvent>,
    private val data: DefaultVoiceGatewayData,
    private val send: suspend (Command) -> Unit
) : GatewayEventHandler(flow, "HandshakeHandler") {
    lateinit var configuration: VoiceGatewayConfiguration


    private val identify
        get() = Identify(
            data.guildId,
            data.selfId,
            data.sessionId,
            configuration.token,
            SUPPORTED_DAVE_PROTOCOL_VERSION
        )

    override suspend fun start() = coroutineScope {
        on<Hello> {
            data.reconnectRetry.reset()
            send(identify)
        }
    }
}
