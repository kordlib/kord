package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.common.ratelimit.RateLimiter
import com.gitlab.kordlib.gateway.retry.LinearRetry
import com.gitlab.kordlib.gateway.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.time.Duration
import kotlin.time.seconds
import kotlin.time.toKotlinDuration

class DefaultGatewayBuilder {
    var url = "wss://gateway.discord.gg/?v=6&encoding=json&compress=zlib-stream"
    var client: HttpClient? = null
    var reconnectRetry: Retry? = null
    var sendRateLimiter: RateLimiter? = null
    var identifyRateLimiter: RateLimiter? = null


    @OptIn(KtorExperimentalAPI::class, ObsoleteCoroutinesApi::class)
    fun build(): DefaultGateway {
        val client = client ?: HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature)
        }
        val retry = reconnectRetry ?: LinearRetry(2.seconds, 20.seconds, 10)
        val sendRateLimiter = sendRateLimiter ?: BucketRateLimiter(120, 60.seconds)
        val identifyRateLimiter = identifyRateLimiter ?: BucketRateLimiter(1, 5.seconds)

        return DefaultGateway(DefaultGatewayData(url, client, retry, sendRateLimiter, identifyRateLimiter))
    }

}