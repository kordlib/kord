package dev.kord.core.extension.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.message.*
import dev.kord.core.live.LiveMessage
import dev.kord.core.live.live
import dev.kord.core.live.on

@KordPreview
suspend fun Message.live(block: LiveMessage.() -> Unit) = this.live().apply(block)

@KordPreview
inline fun LiveMessage.reaction(
    emoji: ReactionEmoji? = null,
    crossinline block: suspend (Event) -> Unit
) = on<Event> {
    if (it is ReactionAddEvent && (emoji == null || emoji == it.emoji) ||
        it is ReactionRemoveEvent && (emoji == null || emoji == it.emoji)
    ) {
        block(it)
    }
}

@KordPreview
inline fun LiveMessage.reactionAdd(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
inline fun LiveMessage.reactionRemove(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveMessage.reactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.create(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.update(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveMessage.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is MessageDeleteEvent || it is MessageBulkDeleteEvent
        || it is ChannelDeleteEvent || it is GuildDeleteEvent
    ) {
        block(it)
    }
}

@KordPreview
fun LiveMessage.onlyDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.bulkDelete(block: suspend (MessageBulkDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.channelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMessage.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
