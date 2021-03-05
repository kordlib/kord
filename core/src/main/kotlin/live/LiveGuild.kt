package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.map
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
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

@KordPreview
fun Guild.live(): LiveGuild = LiveGuild(this)

@KordPreview
inline fun Guild.live(block: LiveGuild.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveGuild.onEmojisUpdate(block: suspend (EmojisUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onIntegrationsUpdate(block: suspend (IntegrationsUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onBanRemove(block: suspend (BanRemoveEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onPresenceUpdate(block: suspend (PresenceUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onVoiceServerUpdate(block: suspend (VoiceServerUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onVoiceStateUpdate(block: suspend (VoiceStateUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onWebhookUpdate(block: suspend (WebhookUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onRoleCreate(block: suspend (RoleCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onRoleUpdate(block: suspend (RoleUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onRoleDelete(block: suspend (RoleDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMemberJoin(block: suspend (MemberJoinEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMemberUpdate(block: suspend (MemberUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMemberLeave(block: suspend (MemberLeaveEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onReactionAdd(block: suspend (ReactionAddEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuild.onReactionAdd(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveGuild.onReactionRemove(block: suspend (ReactionRemoveEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuild.onReactionRemove(
    reaction: ReactionEmoji,
    crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent> {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveGuild.onReactionRemoveAll(block: suspend (ReactionRemoveAllEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMessageCreate(block: suspend (MessageCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMessageUpdate(block: suspend (MessageUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onMessageDelete(block: suspend (MessageDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onChannelCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onChannelUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onGuildCreate(block: suspend (GuildCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onGuildUpdate(block: suspend (GuildUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuild.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveGuild(guild: Guild) : AbstractLiveKordEntity(), KordEntity by guild {

    var guild: Guild = guild
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is EmojisUpdateEvent -> event.guildId == guild.id

        is IntegrationsUpdateEvent -> event.guildId == guild.id

        is BanRemoveEvent -> event.guildId == guild.id

        is PresenceUpdateEvent -> event.guildId == guild.id

        is VoiceServerUpdateEvent -> event.guildId == guild.id
        is VoiceStateUpdateEvent -> event.state.guildId == guild.id

        is WebhookUpdateEvent -> event.guildId == guild.id

        is RoleCreateEvent -> event.guildId == guild.id
        is RoleUpdateEvent -> event.guildId == guild.id
        is RoleDeleteEvent -> event.guildId == guild.id

        is MemberJoinEvent -> event.guildId == guild.id
        is MemberUpdateEvent -> event.guildId == guild.id
        is MemberLeaveEvent -> event.guildId == guild.id

        is ReactionAddEvent -> event.guildId == guild.id
        is ReactionRemoveEvent -> event.guildId == guild.id
        is ReactionRemoveAllEvent -> event.guildId == guild.id

        is MessageCreateEvent -> event.guildId == guild.id
        is MessageUpdateEvent -> event.new.guildId.value == guild.id
        is MessageDeleteEvent -> event.guildId == guild.id

        is ChannelCreateEvent -> event.channel.data.guildId.value == guild.id
        is ChannelUpdateEvent -> event.channel.data.guildId.value == guild.id
        is ChannelDeleteEvent -> event.channel.data.guildId.value == guild.id

        is GuildCreateEvent -> event.guild.id == guild.id
        is GuildUpdateEvent -> event.guild.id == guild.id
        is GuildDeleteEvent -> event.guildId == guild.id

        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is EmojisUpdateEvent -> guild = Guild(guild.data.copy(emojis = event.emojis.map { it.id }), kord)

        is RoleCreateEvent -> guild = Guild(
            guild.data.copy(
                roles = guild.data.roles + event.guildId
            ), kord
        )

        is RoleDeleteEvent -> guild = Guild(
            guild.data.copy(
                roles = guild.data.roles - event.guildId
            ), kord
        )

        is MemberJoinEvent -> guild = Guild(
            guild.data.copy(
                memberCount = guild.data.memberCount.map { it + 1 },
            ), kord
        )

        is MemberLeaveEvent -> guild = Guild(guild.data.copy(
            memberCount = guild.data.memberCount.map { it - 1 }
        ), kord)

        is ChannelCreateEvent -> guild = Guild(guild.data.copy(
            channels = guild.data.channels.map { it + event.channel.id }
        ), kord)

        is ChannelDeleteEvent -> guild = Guild(guild.data.copy(
            channels = guild.data.channels.map { it - event.channel.id }
        ), kord)

        is GuildUpdateEvent -> guild = event.guild
        is GuildDeleteEvent -> shutDown()
        else -> Unit
    }

}
