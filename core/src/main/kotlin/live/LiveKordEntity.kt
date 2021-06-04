package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.KordEntity
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.kordLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A Discord entity that only emits events *related* to this entity.
 *
 * For example, a [LiveMessage] will only emit [MessageUpdateEvents][MessageUpdateEvent] of that message, and only emit
 * [reactions][ReactionAddEvent] to that message.
 */
@KordPreview
interface LiveKordEntity : KordEntity, CoroutineScope {
    val events: Flow<Event>

    fun shutDown(cause: CancellationException = CancellationException("The live entity is shut down", null))
}

@KordPreview
abstract class AbstractLiveKordEntity(
    override val kord: Kord,
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
) : LiveKordEntity, CoroutineScope by coroutineScope {

    private val mutex = Mutex()

    @Suppress("EXPERIMENTAL_API_USAGE")
    final override val events: Flow<Event>
        get() = kord.events
            .takeWhile { isActive }
            .filter { filter(it) }
            .onEach { mutex.withLock { update(it) } }

    protected abstract fun filter(event: Event): Boolean
    protected abstract fun update(event: Event)

    init {
        events.launchIn(this)
    }

    override fun shutDown(cause: CancellationException) = cancel(cause)
}

/**
 * Convenience method that will invoke the [consumer] on every event [T], the consumer is launched in the given [scope]
 * or [Kord] by default and will not propagate any exceptions.
 */
@KordPreview
inline fun <reified T : Event> LiveKordEntity.on(scope: CoroutineScope = this, noinline consumer: suspend (T) -> Unit) =
    events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        runCatching { consumer(it) }.onFailure { kordLogger.catching(it) }
    }.catch { kordLogger.catching(it) }.launchIn(scope)


