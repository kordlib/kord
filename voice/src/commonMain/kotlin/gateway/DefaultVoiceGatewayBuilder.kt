package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.common.http.httpEngine
import dev.kord.gateway.retry.LinearRetry
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Duration.Companion.seconds

@KordVoice
public class DefaultVoiceGatewayBuilder(
    public val selfId: Snowflake,
    public val guildId: Snowflake,
    public val sessionId: String,
) {
    public var client: HttpClient? = null
    public var reconnectRetry: Retry? = null
    public var eventFlow: MutableSharedFlow<VoiceEvent> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    public fun build(): DefaultVoiceGateway {
        val client = client ?: HttpClient(httpEngine()) {
            install(WebSockets)
        }
        val retry = reconnectRetry ?: LinearRetry(2.seconds, 20.seconds, 10)

        val data = DefaultVoiceGatewayData(
            selfId,
            guildId,
            sessionId,
            client,
            retry,
            eventFlow
        )

        return DefaultVoiceGateway(data)
    }
}
