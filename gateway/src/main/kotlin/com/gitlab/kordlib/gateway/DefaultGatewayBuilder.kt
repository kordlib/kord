package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.common.ratelimit.RateLimiter
import com.gitlab.kordlib.gateway.retry.LinearRetry
import com.gitlab.kordlib.gateway.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import java.time.Duration
import kotlin.time.seconds
import kotlin.time.toKotlinDuration

class DefaultGatewayBuilder {
    var url = "wss://gateway.discord.gg/"
    var client: HttpClient? = null
    var retry: Retry? = null
    var rateLimiter: RateLimiter? = null


    fun build(): DefaultGateway {
        val client = client ?: HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature)
        }
        val retry = retry ?: LinearRetry(2.seconds, 20.seconds, 10)
        val rateLimiter = rateLimiter ?: BucketRateLimiter(120, Duration.ofSeconds(60).toKotlinDuration())

        return DefaultGateway(url, client, retry, rateLimiter)
    }

}