package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.Entity
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.kordLogger
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
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
interface LiveEntity : Entity {
    val events: Flow<Event>

    fun shutDown()
}

@KordPreview
abstract class AbstractLiveEntity : LiveEntity {
    private val mutex = Mutex()
    private val running = atomic(true)

    @Suppress("EXPERIMENTAL_API_USAGE")
    override val events: Flow<Event>
        get() = kord.events
                .takeWhile { running.value }
                .filter { filter(it) }
                .onEach { mutex.withLock { update(it) } }

    protected abstract fun filter(event: Event): Boolean
    protected abstract fun update(event: Event)
    override fun shutDown() = running.update { false }

}

/**
 * Convenience method that will invoke the [consumer] on every event [T], the consumer is launched in the given [scope]
 * or [Kord] by default and will not propagate any exceptions.
 */
@KordPreview
inline fun <reified T : Event> LiveEntity.on(scope: CoroutineScope = kord, noinline consumer: suspend (T) -> Unit) =
        events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
            runCatching { consumer(it) }.onFailure { kordLogger.catching(it) }
        }.catch { kordLogger.catching(it) }.launchIn(scope)


