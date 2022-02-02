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
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.core.event.message.*
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.*

@KordPreview
public suspend fun Message.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveMessage =
    LiveMessage(this, withStrategy(EntitySupplyStrategy.cacheWithRestFallback).getGuildOrNull()?.id, coroutineScope)

@KordPreview
public suspend fun Message.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveMessage.() -> Unit
): LiveMessage = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveMessage.onReactionAdd(scope: CoroutineScope = this, block: suspend (ReactionAddEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public inline fun LiveMessage.onReactionAdd(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionAddEvent) -> Unit
): Job = on<ReactionAddEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
public fun LiveMessage.onReactionRemove(
    scope: CoroutineScope = this,
    block: suspend (ReactionRemoveEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@KordPreview
public inline fun LiveMessage.onReactionRemove(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionRemoveEvent) -> Unit
): Job = on<ReactionRemoveEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
public fun LiveMessage.onReactionRemoveAll(
    scope: CoroutineScope = this,
    block: suspend (ReactionRemoveAllEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the message is already created, use LiveChannel.onMessageCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
public fun LiveMessage.onCreate(scope: CoroutineScope = this, block: suspend (MessageCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveMessage.onUpdate(scope: CoroutineScope = this, block: suspend (MessageUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public inline fun LiveMessage.onShutdown(
    scope: CoroutineScope = this,
    crossinline block: suspend (Event) -> Unit
): Job =
    on<Event>(scope) {
        if (it is MessageDeleteEvent || it is MessageBulkDeleteEvent
            || it is ChannelDeleteEvent || it is GuildDeleteEvent
        ) {
            block(it)
        }
    }

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveMessage.onOnlyDelete(scope: CoroutineScope = this, block: suspend (MessageDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveMessage.onBulkDelete(
    scope: CoroutineScope = this,
    block: suspend (MessageBulkDeleteEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveMessage.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveMessage.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveMessage(
    message: Message,
    public val guildId: Snowflake?,
    coroutineScope: CoroutineScope = message.kord + SupervisorJob(message.kord.coroutineContext.job)
) : AbstractLiveKordEntity(message.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = message.id

    public var message: Message = message
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

        is MessageCommandInteractionCreateEvent -> event.interaction.messages.keys.contains(message.id)
        is InteractionCreateEvent -> event.interaction.data.message.value?.id == message.id
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is ReactionAddEvent -> process(event)
        is ReactionRemoveEvent -> process(event)
        is ReactionRemoveAllEvent -> message = Message(message.data.copy(reactions = Optional.Missing()), kord)

        is MessageUpdateEvent -> message = Message(message.data + event.new, kord)
        is MessageDeleteEvent, is MessageBulkDeleteEvent -> shutDown(
            LiveCancellationException(
                event,
                "The message is deleted"
            )
        )

        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        is MessageCommandInteractionCreateEvent -> Unit
        is InteractionCreateEvent -> Unit
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
