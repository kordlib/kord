package dev.kord.gateway

import io.ktor.util.logging.*
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
public class Ticker(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : CoroutineScope {


    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

    private val mutex = Mutex()

    private var ticker: ReceiveChannel<Unit>? = null

    public suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit) {
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

    public suspend fun stop() {
        mutex.withLock {
            ticker?.cancel()
        }
    }

}
