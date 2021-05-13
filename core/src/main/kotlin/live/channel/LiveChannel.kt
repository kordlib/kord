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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@KordPreview
fun Channel.live(dispatcher: CoroutineDispatcher = Dispatchers.Default) = when (this) {
    is DmChannel -> this.live(dispatcher)
    is NewsChannel -> this.live(dispatcher)
    is StoreChannel -> this.live(dispatcher)
    is TextChannel -> this.live(dispatcher)
    is VoiceChannel -> this.live(dispatcher)
    else -> error("unsupported channel type")
}

@KordPreview
inline fun Channel.live(dispatcher: CoroutineDispatcher = Dispatchers.Default, block: LiveChannel.() -> Unit) =
    this.live(dispatcher).apply(block)

@KordPreview
fun LiveChannel.onVoiceStateUpdate(block: suspend (VoiceStateUpdateEvent) -> Unit) = on(consumer = block)

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

@Deprecated(
    "The block is never called because the channel is already created",
    ReplaceWith("LiveGuild.onChannelCreate(block)")
)
@KordPreview
fun LiveChannel.onChannelCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onChannelUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("onShutDown(block)")
)
@KordPreview
fun LiveChannel.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is never called because the guild where the channel is located is already created",
    ReplaceWith("Kord.on<GuildCreateEvent>(block)")
)
@KordPreview
fun LiveChannel.onGuildCreate(block: suspend (GuildCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveChannel.onGuildUpdate(block: suspend (GuildUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
abstract class LiveChannel(dispatcher: CoroutineDispatcher = Dispatchers.Default, parent: Job) : AbstractLiveKordEntity(dispatcher, parent) {

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
