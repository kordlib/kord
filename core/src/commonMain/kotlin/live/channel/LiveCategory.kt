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

@KordPreview
public fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveCategory = LiveCategory(this, coroutineScope)

@KordPreview
public inline fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveCategory.() -> Unit
): LiveCategory = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveCategory.onUpdate(scope: CoroutineScope = this, block: suspend (CategoryUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

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
