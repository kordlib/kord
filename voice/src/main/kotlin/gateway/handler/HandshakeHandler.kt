package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

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
            maxDaveProtocolVersion
        )

    override suspend fun start() = coroutineScope {
        on<Hello> {
            data.reconnectRetry.reset()
            send(identify)
        }
    }

    internal companion object {
        /**
         * Cached max DAVE protocol version. Detected once from the native library.
         */
        val maxDaveProtocolVersion: Int by lazy {
            try {
                moe.kyokobot.libdave.NativeDaveFactory().maxSupportedProtocolVersion()
            } catch (_: Throwable) {
                0
            }
        }
    }
}
