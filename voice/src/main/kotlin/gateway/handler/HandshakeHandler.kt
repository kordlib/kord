package dev.kord.voice.gateway.handler

import dev.kord.common.annotation.KordVoice
import dev.kord.common.ratelimit.consume
import dev.kord.voice.gateway.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.flow.Flow

@OptIn(KordVoice::class)
internal class HandshakeHandler(
    flow: Flow<VoiceEvent>,
    private val data: DefaultVoiceGatewayData,
    private val send: suspend (Command) -> Unit
) : Handler(flow, "HandshakeHandler") {
    lateinit var configuration: VoiceGatewayConfiguration

    private val identify
        get() = Identify(
            data.guildId,
            data.selfId,
            data.sessionId,
            configuration.token
        )

    private val ssrc: AtomicRef<Int?> = atomic(null)

    private val sessionStart get() = ssrc.value == null

    private val resume
        get() = Resume(data.guildId, data.sessionId, configuration.token)

    override fun start() {
        on<Hello> {
            data.reconnectRetry.reset()
            data.identifyRateLimiter.consume {
                if (sessionStart) send(identify)
                else send(resume)
            }
        }

        on<Ready> { ready ->
            ssrc.update { ready.ssrc }
        }

        on<Close> {
            when (it) {
                Close.UserClose -> ssrc.update { null }
                is Close.DiscordClose -> when (it.closeCode) {
                    is VoiceGatewayCloseCode.SessionNoLongerValid -> ssrc.update { null }
                    else -> {
                        /* ignore */
                    }
                }
                else -> {
                    /* ignore */
                }
            }
        }
    }
}