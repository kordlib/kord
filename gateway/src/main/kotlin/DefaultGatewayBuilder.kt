package dev.kord.gateway

import dev.kord.common.KordConfiguration
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.common.ratelimit.RateLimiter
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.gateway.retry.LinearRetry
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.DeprecationLevel.HIDDEN
import kotlin.time.Duration.Companion.seconds

public class DefaultGatewayBuilder {
    public var url: String =
        "wss://gateway.discord.gg/?v=${KordConfiguration.GATEWAY_VERSION}&encoding=json&compress=zlib-stream"
    public var client: HttpClient? = null
    public var reconnectRetry: Retry? = null
    public var sendRateLimiter: RateLimiter? = null
    public var identifyRateLimiter: IdentifyRateLimiter? = null

    private var oldIdentifyRateLimiter: RateLimiter? = null

    @Deprecated("Binary compatibility", level = HIDDEN)
    @get:JvmName("getIdentifyRateLimiter")
    @set:JvmName("setIdentifyRateLimiter")
    public var identifyRateLimiter0: RateLimiter?
        get() = oldIdentifyRateLimiter
        set(value) {
            oldIdentifyRateLimiter = value
        }

    public var dispatcher: CoroutineDispatcher = Dispatchers.Default
    public var eventFlow: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    public fun build(): DefaultGateway {
        val client = client ?: HttpClient(CIO) {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
        val retry = reconnectRetry ?: LinearRetry(2.seconds, 20.seconds, 10)
        val sendRateLimiter = sendRateLimiter ?: IntervalRateLimiter(limit = 120, interval = 60.seconds)
        val identifyRateLimiter = identifyRateLimiter
            ?: @Suppress("DEPRECATION_ERROR") oldIdentifyRateLimiter
                ?.let { dev.kord.gateway.ratelimit.IdentifyRateLimiterFromCommonRateLimiter(it) }
            ?: IdentifyRateLimiter(maxConcurrency = 1, dispatcher)

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

        val data = DefaultGatewayData(
            url,
            client,
            retry,
            sendRateLimiter,
            identifyRateLimiter,
            dispatcher,
            eventFlow
        )

        return DefaultGateway(data)
    }

}
