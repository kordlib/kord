package dev.kord.gateway

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger { }

/**
 * A reusable fixed rate ticker.
 *
 * @param dispatcher The dispatchers the events will be fired on.
 */
public class Ticker(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : CoroutineScope {


    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

    private val mutex = Mutex()

    private var listener: Job? = null

    public suspend fun tickAt(intervalMillis: Long, block: suspend () -> Unit) {
        mutex.withLock {
            listener?.cancel()
            listener = tickingFlow(intervalMillis).onEach {
                try {
                    block()
                } catch (exception: Exception) {
                    logger.error(exception) { "" }
                }
            }.launchIn(this)
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
        delay(period)
        emit(Unit)
    }
}
