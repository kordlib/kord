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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.job

@KordPreview
fun Category.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job
) = LiveCategory(this, dispatcher, parent)

@KordPreview
inline fun Category.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job,
    block: LiveCategory.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveCategory.onCreate(block: suspend (CategoryCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveCategory.onUpdate(block: suspend (CategoryUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveCategory.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is CategoryDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveCategory.onDelete(block: suspend (CategoryDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveCategory.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveCategory(
    channel: Category,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = channel.kord.coroutineContext.job,
) : LiveChannel(channel.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: Category = channel
        private set

    override fun update(event: Event) = when (event) {
        is CategoryCreateEvent -> channel = event.channel
        is CategoryUpdateEvent -> channel = event.channel
        is CategoryDeleteEvent -> shutDown(LiveCancellationException(event, "The category is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
