package com.gitlab.hopebaron.websocket.ratelimit

interface RateLimiter {
    suspend fun consume()
}

suspend inline fun <T> RateLimiter.consume(block: () -> T): T {
    consume()
    return block()
}

