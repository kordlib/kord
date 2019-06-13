package com.gitlab.hopebaron.websocket

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * A reusable fixed rate ticker.
 *
 * @param dispatcher The dispatchers the events will be fired on.
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
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