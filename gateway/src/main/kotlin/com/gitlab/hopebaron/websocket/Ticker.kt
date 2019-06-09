package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.ratelimit.BucketRateLimiter
import com.gitlab.hopebaron.websocket.retry.LinearRetry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.time.Duration
import kotlin.coroutines.CoroutineContext


@ObsoleteCoroutinesApi
class Ticker(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + dispatcher

    private val mutex = Mutex()

    private var ticker: ReceiveChannel<Unit>? = null

    suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit) {
        stop()
        mutex.withLock {
            ticker = ticker(intervalMillis)
            launch {
                ticker?.consumeEach {
                    block()
                }
            }
        }
    }

    suspend fun stop() {
        mutex.withLock {
            ticker?.cancel()
        }
    }

}