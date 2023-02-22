package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.Category
import dev.kord.core.event.Event
import dev.kord.core.event.channel.CategoryCreateEvent
import dev.kord.core.event.channel.CategoryDeleteEvent
import dev.kord.core.event.channel.CategoryUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.*

/**
 * Returns a [LiveCategory] for the given [Category]
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveCategory] with
 * @return The created [LiveCategory]
 */
@KordPreview
public fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveCategory = LiveCategory(this, coroutineScope)

/**
 * Returns a [LiveCategory] for a given [Category] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveCategory] with.
 * @param block The [LiveCategory] configuration
 * @return The created [LiveCategory]
 */
@KordPreview
public inline fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveCategory.() -> Unit
): LiveCategory = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveCategory.onUpdate(scope: CoroutineScope = this, block: suspend (CategoryUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * A Live entity for a [Category].
 *
 * @property channel The [Category] to get a live object for
 * @property coroutineScope The [CoroutineScope] to create the live object with
 */
@KordPreview
public class LiveCategory(
    channel: Category,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: Category = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is CategoryCreateEvent -> channel = event.channel
        is CategoryUpdateEvent -> channel = event.channel
        is CategoryDeleteEvent -> shutDown(LiveCancellationException(event, "The category is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
