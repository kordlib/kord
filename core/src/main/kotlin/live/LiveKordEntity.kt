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

/**
 * A Discord entity that only emits events *related* to this entity.
 *
 * For example, a [LiveMessage] will only emit [MessageUpdateEvents][MessageUpdateEvent] of that message, and only emit
 * [reactions][ReactionAddEvent] to that message.
 */
@KordPreview
public interface LiveKordEntity : KordEntity, CoroutineScope {
    public val events: Flow<Event>

    public fun shutDown(cause: CancellationException = CancellationException("The live entity is shut down", null))
}

@KordPreview
public abstract class AbstractLiveKordEntity(
    final override val kord: Kord,
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
) : LiveKordEntity, CoroutineScope by coroutineScope {

    final override val events: SharedFlow<Event> =
        kord.events.filter { filter(it) }.onEach { update(it) }.shareIn(this, SharingStarted.Eagerly)

    protected abstract fun filter(event: Event): Boolean
    protected abstract fun update(event: Event)

    override fun shutDown(cause: CancellationException): Unit = cancel(cause)
}

/**
 * Convenience method that will invoke the [consumer] on every event [T], the consumer is launched in the given [scope]
 * or [Kord] by default and will not propagate any exceptions.
 */
@KordPreview
public inline fun <reified T : Event> LiveKordEntity.on(
    scope: CoroutineScope = this,
    noinline consumer: suspend (T) -> Unit
): Job =
    events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        runCatching { consumer(it) }.onFailure { kordLogger.catching(it) }
    }.catch { kordLogger.catching(it) }.launchIn(scope)
