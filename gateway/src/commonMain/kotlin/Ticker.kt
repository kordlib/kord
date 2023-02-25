package dev.kord.gateway

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private var ticker: Flow<Unit>? = null
    private var listener: Job? = null

    public suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit) {
        stop()
        mutex.withLock {
            ticker = tickingFlow(intervalMillis)

            listener = ticker?.onEach {
                try {
                    block()
                } catch (exception: Exception) {
                    logger.error(exception)
                }
            }?.launchIn(this)
        }
    }

    public suspend fun stop() {
        mutex.withLock {
            listener?.cancel()
        }
    }

}

private fun tickingFlow(period: Long): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(period)
    }
}
