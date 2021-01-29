package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.GuildUpdateEvent
import dev.kord.core.event.message.*
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.live.on

@KordPreview
fun Channel.live() = when (this) {
    is DmChannel -> this.live()
    is NewsChannel -> this.live()
    is StoreChannel -> this.live()
    is TextChannel -> this.live()
    is VoiceChannel -> this.live()
    else -> error("unsupported channel type")
}

@KordPreview
inline fun Channel.live(block: LiveChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveChannel.onVoiceStateUpdate(block: suspend (VoiceStateUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveChannel.onReaction(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ReactionAddEvent || it is ReactionRemoveEvent) {
        block(it)
    }
}

@KordPreview
inline fun LiveChannel.onReaction(
    emoji: ReactionEmoji,
    crossinline block: suspend (Event) -> Unit
) = on<Event> {
    if (it is ReactionAddEvent && (emoji == it.emoji) || it is ReactionRemoveEvent && (emoji == it.emoji)) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.onReactionAdd(block: suspend (ReactionAddEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveChannel.onReactionAdd(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.onReactionRemove(block: suspend (ReactionRemoveEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveChannel.onReactionRemove(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.onReactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onMessageCreate(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onMessageUpdate(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onMessageDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onChannelCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onChannelUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onGuildCreate(block: suspend (GuildCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onGuildUpdate(block: suspend (GuildUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
abstract class LiveChannel : AbstractLiveKordEntity() {

    abstract val channel: Channel

    override fun filter(event: Event): Boolean = when (event) {
        is VoiceStateUpdateEvent -> event.state.channelId == channel.id

        is ReactionAddEvent -> event.channelId == channel.id
        is ReactionRemoveEvent -> event.channelId == channel.id
        is ReactionRemoveAllEvent -> event.channelId == channel.id

        is MessageCreateEvent -> event.message.channelId == channel.id
        is MessageUpdateEvent -> event.new.channelId == channel.id
        is MessageDeleteEvent -> event.channelId == channel.id

        is ChannelCreateEvent -> event.channel.id == channel.id
        is ChannelUpdateEvent -> event.channel.id == channel.id
        is ChannelDeleteEvent -> event.channel.id == channel.id

        is GuildCreateEvent -> event.guild.id == channel.data.guildId.value
        is GuildUpdateEvent -> event.guild.id == channel.data.guildId.value
        is GuildDeleteEvent -> event.guildId == channel.data.guildId.value

        else -> false
    }

}
