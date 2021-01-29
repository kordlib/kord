package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.GuildUpdateEvent
import dev.kord.core.event.message.*
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.live.channel.LiveChannel
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun Channel.live(block: LiveChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveChannel.voiceStateUpdate(block: suspend (VoiceStateUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveChannel.reaction(
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
inline fun LiveChannel.reactionAdd(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
inline fun LiveChannel.reactionRemove(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.reactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.messageCreate(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.messageUpdate(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.messageDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.channelCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.channelUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.channelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.guildCreate(block: suspend (GuildCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.guildUpdate(block: suspend (GuildUpdateEvent) -> Unit) = on(consumer = block)
