package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.cache.data.ReactionData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.ChannelDeleteEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.kordLogger
import com.gitlab.kordlib.core.on
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
    protected fun shutDown() = running.update { false }

}

/**
 * Convenience method that will invoke the [consumer] on every event [T], the consumer is launched in the given [scope]
 * or [Kord] by default and will not propagate any exceptions.
 */
@KordPreview
inline fun <reified T : Event> LiveEntity.on(scope: CoroutineScope = kord,  noinline consumer: suspend (T) -> Unit) =
        events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
            runCatching { consumer(it) }.onFailure { kordLogger.catching(it) }
        }.catch { kordLogger.catching(it) }.launchIn(scope)


