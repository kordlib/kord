package dev.kord.voice.gateway

import kotlinx.coroutines.*
import mu.KotlinLogging

/**
 * A reusable fixed rate ticker.
 */
@ObsoleteCoroutinesApi
class Ticker {
    // we only want one of these
    private var tickerJob: Job? = null

    suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit): Unit = coroutineScope {
        stop()
        tickerJob = launch {
            while (isActive) {
                block()
                delay(intervalMillis)
            }
        }
    }

    fun stop() {
        tickerJob?.cancel()
    }
}