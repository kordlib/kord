package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.Presence
import com.gitlab.kordlib.core.event.PresenceUpdateEvent
import com.gitlab.kordlib.core.event.guild.*
import com.gitlab.kordlib.core.event.role.RoleCreateEvent
import com.gitlab.kordlib.core.event.role.RoleDeleteEvent
import com.gitlab.kordlib.core.event.role.RoleUpdateEvent
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import com.gitlab.kordlib.common.entity.DiscordGuild as GatewayGuild
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class GuildEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is GuildCreate -> handle(event)
        is GuildUpdate -> handle(event)
        is GuildDelete -> handle(event)
        is GuildBanAdd -> handle(event)
        is GuildBanRemove -> handle(event)
        is GuildEmojisUpdate -> handle(event)
        is GuildIntegrationsUpdate -> handle(event)
        is GuildMemberAdd -> handle(event)
        is GuildMemberRemove -> handle(event)
        is GuildMemberUpdate -> handle(event)
        is GuildRoleCreate -> handle(event)
        is GuildRoleUpdate -> handle(event)
        is GuildRoleDelete -> handle(event)
        is GuildMembersChunk -> handle(event)
        is PresenceUpdate -> handle(event)
        is InviteCreate -> handle(event)
        is InviteDelete -> handle(event)
        else -> Unit
    }

    private suspend fun GatewayGuild.cache() {
        for (member in members.orEmpty()) {
            cache.put(MemberData.from(member.user!!.id, id, member))
            cache.put(UserData.from(member.user!!))
        }

        for (role in roles) {
            cache.put(RoleData.from(id, role))
        }

        for (channel in channels.orEmpty()) {
            cache.put(ChannelData.from(channel))
        }

        for (presence in presences.orEmpty()) {
            cache.put(PresenceData.from(presence))
        }

        for (voiceState in voiceStates.orEmpty()) {
            cache.put(VoiceStateData.from(voiceState))
        }
    }

    private suspend fun handle(event: GuildCreate) {
        val data = GuildData.from(event.guild)
        cache.put(data)
        event.guild.cache()

        coreEventChannel.send(GuildCreateEvent(Guild(data, kord)))
    }

    private suspend fun handle(event: GuildUpdate) {
        val data = GuildData.from(event.guild)
        cache.put(data)
        event.guild.cache()

        coreEventChannel.send(GuildCreateEvent(Guild(data, kord)))
    }

    private suspend fun handle(event: GuildDelete) = with(event.guild) {
        val query = cache.find<GuildData> { GuildData::id eq id.toLong() }

        val old = query.asFlow().map { Guild(it, kord) }.singleOrNull()
        query.remove()

        coreEventChannel.send(GuildDeleteEvent(Snowflake(id), unavailable ?: false, old, kord))
    }

    private suspend fun handle(event: GuildBanAdd) = with(event.ban) {
        val data = UserData.from(user)
        cache.put(user)
        val user = User(data, kord)

        coreEventChannel.send(BanEvent(user, Snowflake(guildId)))
    }

    private suspend fun handle(event: GuildBanRemove) = with(event.ban) {
        val data = UserData.from(user)
        cache.put(user)
        val user = User(data, kord)

        coreEventChannel.send(UnbanEvent(user, Snowflake(guildId)))
    }

    private suspend fun handle(event: GuildEmojisUpdate) = with(event.emoji) {
        val guildId = Snowflake(guildId)
        val emojis = emojis.map { GuildEmoji(EmojiData.from(it.id!!, it), guildId, kord) }.toSet()

        coreEventChannel.send(EmojisUpdateEvent(guildId, emojis, kord))
    }

    private suspend fun handle(event: GuildIntegrationsUpdate) {
        coreEventChannel.send(IntegrationsUpdateEvent(Snowflake(event.integrations.guildId), kord))
    }

    private suspend fun handle(event: GuildMemberAdd) = with(event.member) {
        val userData = UserData.from(user!!)
        val memberData = MemberData.from(user!!.id, event.member)

        cache.put(userData)
        cache.put(memberData)

        val member = Member(memberData, userData, kord)

        coreEventChannel.send(MemberJoinEvent(member))
    }

    private suspend fun handle(event: GuildMemberRemove) = with(event.member) {
        val userData = UserData.from(user)
        cache.find<UserData> { UserData::id eq userData.id }.remove()
        val user = User(userData, kord)

        coreEventChannel.send(MemberLeaveEvent(user, Snowflake(guildId)))
    }

    private suspend fun handle(event: GuildMemberUpdate) = with(event.member) {
        val userData = UserData.from(user)
        cache.put(userData)

        val old = cache.find<MemberData> {
            MemberData::userId eq userData.id
            MemberData::guildId eq guildId.toLong()
        }.asFlow().map { Member(it, userData, kord) }.singleOrNull()

        cache.find<MemberData> {
            MemberData::userId eq userData.id
            MemberData::guildId eq guildId.toLong()
        }.update { it + this }

        val roles = roles.asSequence().map { Snowflake(it) }.toSet()

        coreEventChannel.send(
                MemberUpdateEvent(
                        old,
                        Snowflake(guildId),
                        Snowflake(userData.id),
                        roles,
                        nick ?: userData.username,
                        premiumSince?.toInstant(),
                        kord
                )
        )
    }

    private suspend fun handle(event: GuildRoleCreate) {
        val data = RoleData.from(event.role)
        cache.put(data)

        coreEventChannel.send(RoleCreateEvent(Role(data, kord)))
    }

    private suspend fun handle(event: GuildRoleUpdate) {
        val data = RoleData.from(event.role)
        cache.put(data)

        coreEventChannel.send(RoleUpdateEvent(Role(data, kord)))
    }

    private suspend fun handle(event: GuildRoleDelete) = with(event.role) {
        val query = cache.find<RoleData> { RoleData::id eq event.role.id.toLong() }

        val old = kotlin.run {
            val data = query.singleOrNull() ?: return@run null
            Role(data, kord)
        }

        query.remove()

        coreEventChannel.send(RoleDeleteEvent(Snowflake(guildId), Snowflake(id), old, kord))
    }

    private suspend fun handle(event: GuildMembersChunk) = with(event.data) {
        val members = members.asFlow().map { member ->
            val memberData = MemberData.from(member.user!!.id, guildId, member)
            cache.put(memberData)
            val userData = UserData.from(member.user!!)
            cache.put(userData)

            Member(memberData, userData, kord)
        }.toSet()

        coreEventChannel.send(MemberChunksEvent(Snowflake(guildId), members, kord))
    }

    private suspend fun handle(event: PresenceUpdate) = with(event.presence) {
        val data = PresenceData.from(this)

        val old = cache.find<PresenceData> { PresenceData::id eq data.id }
                .asFlow().map { Presence(it, kord) }.singleOrNull()

        cache.put(data)
        val new = Presence(data, kord)

        val user = cache
                .find<UserData> { UserData::id eq event.presence.user.id.toLong() }
                .singleOrNull()
                ?.let { User(it, kord) }

        coreEventChannel.send(PresenceUpdateEvent(user, this.user, Snowflake(guildId!!), old, new))
    }

    private suspend fun handle(event: InviteCreate) = with(event) {
        val data = InviteCreateData.from(invite)

        with(invite.inviter) {
            cache.find<UserData> { UserData::id eq id.toLong() }
                    .update { it.copy(discriminator = discriminator, username = username, avatar = avatar) }
        }

        coreEventChannel.send(InviteCreateEvent(data, kord))
    }

    private suspend fun handle(event: InviteDelete) = with(event) {
        val data = InviteDeleteData.from(invite)
        coreEventChannel.send(InviteDeleteEvent(data, kord))
    }

}