package dev.kord.gateway

import dev.kord.common.KordConfiguration
import dev.kord.common.http.httpEngine
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.common.ratelimit.RateLimiter
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.gateway.retry.LinearRetry
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Duration.Companion.seconds

public class DefaultGatewayBuilder {
    public var url: String =
        "wss://gateway.discord.gg/?v=${KordConfiguration.GATEWAY_VERSION}&encoding=json&compress=zlib-stream"
    public var client: HttpClient? = null
    public var reconnectRetry: Retry? = null
    public var sendRateLimiter: RateLimiter? = null
    public var identifyRateLimiter: IdentifyRateLimiter? = null
    public var dispatcher: CoroutineDispatcher = Dispatchers.Default
    public var eventFlow: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    public fun build(): DefaultGateway {
        val client = client ?: HttpClient(httpEngine()) {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
        val retry = reconnectRetry ?: LinearRetry(2.seconds, 20.seconds, 10)
        val sendRateLimiter = sendRateLimiter ?: IntervalRateLimiter(limit = 120, interval = 60.seconds)
        val identifyRateLimiter = identifyRateLimiter ?: IdentifyRateLimiter(maxConcurrency = 1, dispatcher)

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
