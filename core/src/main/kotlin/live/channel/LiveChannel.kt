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
import kotlinx.coroutines.*

@KordPreview
public fun Channel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveChannel = when (this) {
    is DmChannel -> this.live(coroutineScope)
    is NewsChannel -> this.live(coroutineScope)
    is @Suppress("DEPRECATION") StoreChannel -> this.live(coroutineScope)
    is TextChannel -> this.live(coroutineScope)
    is VoiceChannel -> this.live(coroutineScope)
    else -> error("unsupported channel type")
}

@KordPreview
public inline fun Channel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveChannel.() -> Unit
): LiveChannel = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveChannel.onVoiceStateUpdate(scope: CoroutineScope = this, block: suspend (VoiceStateUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onReactionAdd(scope: CoroutineScope = this, block: suspend (ReactionAddEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public inline fun LiveChannel.onReactionAdd(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this,
    crossinline block: suspend (ReactionAddEvent) -> Unit
): Job = on<ReactionAddEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
public fun LiveChannel.onReactionRemove(scope: CoroutineScope = this, block: suspend (ReactionRemoveEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public inline fun LiveChannel.onReactionRemove(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
): Job = on<ReactionRemoveEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
public fun LiveChannel.onReactionRemoveAll(scope: CoroutineScope = this, block: suspend (ReactionRemoveAllEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onMessageCreate(scope: CoroutineScope = this, block: suspend (MessageCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onMessageUpdate(scope: CoroutineScope = this, block: suspend (MessageUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onMessageDelete(scope: CoroutineScope = this, block: suspend (MessageDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
public fun LiveChannel.onChannelCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onChannelUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveChannel.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is never called because the guild where the channel is located is already created",
    ReplaceWith("Kord.on<GuildCreateEvent>(block)"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveChannel.onGuildCreate(scope: CoroutineScope = this, block: suspend (GuildCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveChannel.onGuildUpdate(scope: CoroutineScope = this, block: suspend (GuildUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public abstract class LiveChannel(
    kord: Kord,
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
) : AbstractLiveKordEntity(kord, coroutineScope) {

    public abstract val channel: Channel

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
