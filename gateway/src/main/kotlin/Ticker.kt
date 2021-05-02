package dev.kord.gateway

import io.ktor.util.error
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger { }

/**
 * A reusable fixed rate ticker.
 *
 * @param dispatcher The dispatchers the events will be fired on.
 */
@ObsoleteCoroutinesApi
class Ticker(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = job + dispatcher

    private val mutex = Mutex()

    private var ticker: ReceiveChannel<Unit>? = null

    suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit) {
        stop()
        mutex.withLock {
            ticker = ticker(intervalMillis)
            launch {
                ticker?.consumeEach {
                    try {
                        block()
                    } catch (exception: Exception) {
                        logger.error(exception)
                    }
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