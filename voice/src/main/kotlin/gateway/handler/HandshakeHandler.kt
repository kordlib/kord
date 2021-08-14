package dev.kord.voice.gateway.handler

import dev.kord.common.ratelimit.consume
import dev.kord.voice.gateway.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
    flow: Flow<Event>,
    private val data: DefaultVoiceGatewayData,
    private val send: suspend (Command) -> Unit
) : Handler(flow, "HandshakeHandler") {
    lateinit var configuration: VoiceGatewayConfiguration

    private val identify
        get() = Identify(
            data.guildId,
            data.selfId,
            configuration.sessionId,
            configuration.token
        )

    private val session: AtomicRef<String?> = atomic(null)

    private val sessionStart get() = session.value == null

    private val resume
        get() = Resume(data.guildId, session.value!!, configuration.token)

    override fun start() {
        on<Hello> {
            data.reconnectRetry.reset()
            data.identifyRateLimiter.consume {
                if (sessionStart) send(identify)
                else send(resume)
            }
        }
    }
}
