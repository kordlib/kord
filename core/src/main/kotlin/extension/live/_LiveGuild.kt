package dev.kord.core.extension.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.*
import dev.kord.core.event.message.*
import dev.kord.core.event.role.RoleCreateEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent
import dev.kord.core.event.user.PresenceUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.live.LiveGuild
import dev.kord.core.live.live
import dev.kord.core.live.on

@KordPreview
inline fun Guild.live(block: LiveGuild.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveGuild.emojisUpdate(block: suspend (EmojisUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.integrationsUpdate(block: suspend (IntegrationsUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.banRemove(block: suspend (BanRemoveEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.presenceUpdate(block: suspend (PresenceUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.voiceServerUpdate(block: suspend (VoiceServerUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.voiceStateUpdate(block: suspend (VoiceStateUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.webhookUpdate(block: suspend (WebhookUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.roleCreate(block: suspend (RoleCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.roleUpdate(block: suspend (RoleUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.roleDelete(block: suspend (RoleDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.memberJoin(block: suspend (MemberJoinEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.memberUpdate(block: suspend (MemberUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.memberLeave(block: suspend (MemberLeaveEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuild.reaction(
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
inline fun LiveGuild.reactionAdd(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
inline fun LiveGuild.reactionRemove(
    reaction: ReactionEmoji? = null,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (reaction == null || it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveGuild.reactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.messageCreate(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.messageUpdate(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.messageDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.channelCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.channelUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.channelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.guildCreate(block: suspend (GuildCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.guildUpdate(block: suspend (GuildUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.shutDown(block: suspend (GuildDeleteEvent) -> Unit) = guildDelete(block)
