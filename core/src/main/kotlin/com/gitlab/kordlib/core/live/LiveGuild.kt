package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.common.entity.optional.*
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.event.*
import com.gitlab.kordlib.core.event.channel.ChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.ChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.ChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.*
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.event.role.RoleCreateEvent
import com.gitlab.kordlib.core.event.role.RoleDeleteEvent
import com.gitlab.kordlib.core.event.role.RoleUpdateEvent
import com.gitlab.kordlib.core.event.user.PresenceUpdateEvent
import com.gitlab.kordlib.core.event.user.VoiceStateUpdateEvent

@KordPreview
fun Guild.live(): LiveGuild = LiveGuild(this)

@KordPreview
class LiveGuild(guild: Guild) : AbstractLiveEntity(), Entity by guild {

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

        is RoleCreateEvent -> guild = Guild(guild.data.copy(
                roles = guild.data.roles + event.guildId
        ), kord)

        is RoleDeleteEvent -> guild = Guild(guild.data.copy(
                roles = guild.data.roles - event.guildId
        ), kord)

        is MemberJoinEvent -> guild = Guild(guild.data.copy(
                memberCount = guild.data.memberCount.map { it + 1 },
        ), kord)

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
