package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.common.http.HttpEngine
import dev.kord.gateway.retry.LinearRetry
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
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
        val client = client ?: HttpClient(HttpEngine) {
            install(WebSockets)
        }
        val retry = reconnectRetry ?: LinearRetry(2.seconds, 20.seconds, 10)

        client.requestPipeline.intercept(HttpRequestPipeline.Render) {
            // CIO adds this header even if no extensions are used, which causes it to be empty
            // This immediately kills the gateway connection
            if (context.url.protocol.isWebsocket()) {
                val header = context.headers[HttpHeaders.SecWebSocketExtensions]
                // If it's blank Discord ragequits
                if (header?.isBlank() == true) {
                    context.headers.remove(HttpHeaders.SecWebSocketExtensions)
                }
            }
            proceed()
        }

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
