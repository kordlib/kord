package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
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

fun Guild.toLive(): LiveGuild = LiveGuild(this)

@KordPreview
class LiveGuild(guild: Guild) : AbstractLiveEntity(), Entity by guild {

    var guild: Guild = guild
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is EmojisUpdateEvent -> event.guildId == guild.id

        is IntegrationsUpdateEvent -> event.guildId == guild.id

        is UnbanEvent -> event.guildId == guild.id

        is PresenceUpdateEvent -> event.guildId == guild.id

        is UserUpdateEvent -> guild.data.members.any { it.userId == event.user.id.longValue }

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

        is MessageCreateEvent -> event.message.guildId == guild.id
        is MessageUpdateEvent -> event.new.guildId == guild.id.value
        is MessageDeleteEvent -> event.guildId == guild.id

        is ChannelCreateEvent -> event.channel.data.guildId == guild.id.longValue
        is ChannelUpdateEvent -> event.channel.data.guildId == guild.id.longValue
        is ChannelDeleteEvent -> event.channel.data.guildId == guild.id.longValue

        is GuildCreateEvent -> event.guild.id == guild.id
        is GuildUpdateEvent -> event.guild.id == guild.id
        is GuildDeleteEvent -> event.guildId == guild.id

        else -> true
    }

    override fun update(event: Event): Unit = when (event) {
        is EmojisUpdateEvent -> guild = Guild(guild.data.copy(emojis = event.emojis.map { it.data }), kord)

        is RoleCreateEvent -> guild = Guild(guild.data.copy(
                roles = guild.data.roles + event.guildId.longValue
        ), kord)

        is RoleDeleteEvent -> guild = Guild(guild.data.copy(
                roles = guild.data.roles - event.guildId.longValue
        ), kord)

        is PresenceUpdateEvent -> guild = Guild(guild.data.copy(
                presences = guild.data.presences.map {
                    when (it.userId) {
                        event.user.id.toLong() -> event.presence.data
                        else -> it
                    }
                }
        ), kord)

        is VoiceStateUpdateEvent -> guild = Guild(guild.data.copy(
                voiceStates = guild.data.voiceStates.filter { it.userId == event.state.userId.longValue } + event.state.data
        ), kord)

        is MemberJoinEvent -> guild = Guild(guild.data.copy(
                memberCount = guild.data.memberCount?.inc(),
                members = guild.data.members + event.member.memberData
        ), kord)

        is MemberUpdateEvent -> guild = Guild(guild.data.copy(
                members = guild.data.members.map {
                    when (it.userId) {
                        event.memberId.longValue -> it.copy(nick = event.currentNickName, roles = event.currentRoleIds.map { it.value })
                        else -> it
                    }
                }
        ), kord)

        is MemberLeaveEvent -> guild = Guild(guild.data.copy(
                memberCount = guild.data.memberCount?.dec(),
                members = guild.data.members.filter { it.userId != event.guildId.longValue }
        ), kord)

        is ChannelCreateEvent -> guild = Guild(guild.data.copy(
                channels = guild.data.channels + event.channel.id.longValue
        ), kord)

        is ChannelDeleteEvent -> guild = Guild(guild.data.copy(
                channels = guild.data.channels - event.channel.id.longValue
        ), kord)

        is GuildUpdateEvent -> guild = event.guild
        is GuildDeleteEvent -> shutDown()
        else -> Unit
    }

}
