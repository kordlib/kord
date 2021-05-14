package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ReactionData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.message.*
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@KordPreview
suspend fun Message.live(dispatcher: CoroutineDispatcher = Dispatchers.Default) =
    LiveMessage(this, withStrategy(EntitySupplyStrategy.cacheWithRestFallback).getGuildOrNull()?.id, dispatcher)

@KordPreview
suspend fun Message.live(dispatcher: CoroutineDispatcher = Dispatchers.Default, block: LiveMessage.() -> Unit) =
    this.live(dispatcher).apply(block)

@KordPreview
fun LiveMessage.onReactionAdd(block: suspend (ReactionAddEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveMessage.onReactionAdd(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveMessage.onReactionRemove(block: suspend (ReactionRemoveEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveMessage.onReactionRemove(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveMessage.onReactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is never called because the message is already created",
    ReplaceWith("LiveChannel.onMessageCreate(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveMessage.onCreate(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.onUpdate(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the live entity is shutdown",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveMessage.onShutdown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is MessageDeleteEvent || it is MessageBulkDeleteEvent
        || it is ChannelDeleteEvent || it is GuildDeleteEvent
    ) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveMessage.onOnlyDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveMessage.onBulkDelete(block: suspend (MessageBulkDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveMessage.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveMessage.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveMessage(
    message: Message,
    val guildId: Snowflake?,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : AbstractLiveKordEntity(message.kord, dispatcher), KordEntity {

    override val id: Snowflake
        get() = message.id

    var message: Message = message
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is ReactionAddEvent -> event.messageId == message.id
        is ReactionRemoveEvent -> event.messageId == message.id
        is ReactionRemoveAllEvent -> event.messageId == message.id

        is MessageCreateEvent -> event.message.id == message.id
        is MessageUpdateEvent -> event.messageId == message.id
        is MessageDeleteEvent -> event.messageId == message.id
        is MessageBulkDeleteEvent -> event.messageIds.contains(message.id)

        is ChannelDeleteEvent -> event.channel.id == message.channelId

        is GuildDeleteEvent -> event.guildId == guildId
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is ReactionAddEvent -> process(event)
        is ReactionRemoveEvent -> process(event)
        is ReactionRemoveAllEvent -> message = Message(message.data.copy(reactions = Optional.Missing()), kord)

        is MessageUpdateEvent -> message = Message(message.data + event.new, kord)
        is MessageDeleteEvent -> shutDown()
        is MessageBulkDeleteEvent -> shutDown()

        is ChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()
        else -> Unit
    }

    private fun process(event: ReactionAddEvent) = with(event.emoji) {
        val animated = this is ReactionEmoji.Custom && isAnimated

        val present = message.data.reactions.orEmpty()
            .firstOrNull { it.emojiName == name && it.emojiId == id }

        val reactions = when (present) {
            null -> message.data.reactions.orEmpty() + ReactionData(1, event.userId == kord.selfId, id, name, animated)
            else -> {
                val updated = present.copy(count = present.count + 1)
                message.data.reactions.orEmpty() - present + updated
            }
        }

        message = Message(message.data.copy(reactions = Optional.Value(reactions)), kord)
    }

    private fun process(event: ReactionRemoveEvent) = with(event.emoji) {
        val present = message.data.reactions.orEmpty()
            .firstOrNull { it.emojiName == name && it.emojiId == id }

        val reactions = when (present) {
            null -> message.data.reactions.orEmpty()
            else -> {
                val updated = present.copy(count = present.count - 1)
                message.data.reactions.orEmpty() - present + updated
            }
        }

        message = Message(message.data.copy(reactions = Optional.Value(reactions)), kord)
    }

}
