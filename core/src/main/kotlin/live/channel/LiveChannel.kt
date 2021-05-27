package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@KordPreview
fun Channel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
) = when (this) {
    is DmChannel -> this.live(dispatcher, parent)
    is NewsChannel -> this.live(dispatcher, parent)
    is StoreChannel -> this.live(dispatcher, parent)
    is TextChannel -> this.live(dispatcher, parent)
    is VoiceChannel -> this.live(dispatcher, parent)
    else -> error("unsupported channel type")
}

@KordPreview
inline fun Channel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord,
    block: LiveChannel.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@KordPreview
fun LiveChannel.onVoiceStateUpdate(scope: CoroutineScope = this, block: suspend (VoiceStateUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onReactionAdd(scope: CoroutineScope = this, block: suspend (ReactionAddEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
inline fun LiveChannel.onReactionAdd(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.onReactionRemove(scope: CoroutineScope = this, block: suspend (ReactionRemoveEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
inline fun LiveChannel.onReactionRemove(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveChannel.onReactionRemoveAll(scope: CoroutineScope = this, block: suspend (ReactionRemoveAllEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onMessageCreate(scope: CoroutineScope = this, block: suspend (MessageCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onMessageUpdate(scope: CoroutineScope = this, block: suspend (MessageUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onMessageDelete(scope: CoroutineScope = this, block: suspend (MessageDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveChannel.onChannelCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onChannelUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveChannel.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is never called because the guild where the channel is located is already created",
    ReplaceWith("Kord.on<GuildCreateEvent>(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveChannel.onGuildCreate(scope: CoroutineScope = this, block: suspend (GuildCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveChannel.onGuildUpdate(scope: CoroutineScope = this, block: suspend (GuildUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
abstract class LiveChannel(
    kord: Kord,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
) : AbstractLiveKordEntity(kord, dispatcher, parent) {

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
