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
fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
) = LiveCategory(this, coroutineScope)

@KordPreview
inline fun Category.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveCategory.() -> Unit
) = this.live(coroutineScope).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveCategory.onCreate(scope: CoroutineScope = this, block: suspend (CategoryCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveCategory.onUpdate(scope: CoroutineScope = this, block: suspend (CategoryUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveCategory.onShutDown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit) =
    on<Event>(scope) {
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
fun LiveCategory.onDelete(scope: CoroutineScope = this, block: suspend (CategoryDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveCategory.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveCategory(
    channel: Category,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

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
