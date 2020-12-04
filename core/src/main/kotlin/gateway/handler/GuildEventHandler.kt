package dev.kord.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.putAll
import com.gitlab.kordlib.cache.api.query
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.entity.*
import dev.kord.core.event.guild.*
import dev.kord.core.event.role.RoleCreateEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent
import dev.kord.core.event.user.PresenceUpdateEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.flow.*
import dev.kord.common.entity.DiscordGuild as GatewayGuild
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class GuildEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is GuildCreate -> handle(event, shard)
        is GuildUpdate -> handle(event, shard)
        is GuildDelete -> handle(event, shard)
        is GuildBanAdd -> handle(event, shard)
        is GuildBanRemove -> handle(event, shard)
        is GuildEmojisUpdate -> handle(event, shard)
        is GuildIntegrationsUpdate -> handle(event, shard)
        is GuildMemberAdd -> handle(event, shard)
        is GuildMemberRemove -> handle(event, shard)
        is GuildMemberUpdate -> handle(event, shard)
        is GuildRoleCreate -> handle(event, shard)
        is GuildRoleUpdate -> handle(event, shard)
        is GuildRoleDelete -> handle(event, shard)
        is GuildMembersChunk -> handle(event, shard)
        is PresenceUpdate -> handle(event, shard)
        is InviteCreate -> handle(event, shard)
        is InviteDelete -> handle(event, shard)
        else -> Unit
    }

    private suspend fun GatewayGuild.cache() {
        for (member in members.orEmpty()) {
            cache.put(MemberData.from(member.user.value!!.id, id, member))
            cache.put(UserData.from(member.user.value!!))
        }

        for (role in roles) {
            cache.put(RoleData.from(id, role))
        }

        for (channel in channels.orEmpty()) {
            cache.put(ChannelData.from(channel.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
        }

        for (presence in presences.orEmpty()) {
            cache.put(PresenceData.from(id, presence))
        }

        for (voiceState in voiceStates.orEmpty()) {
            cache.put(VoiceStateData.from(id, voiceState))
        }
        for (emoji in emojis) {
            cache.put(EmojiData.from(id, emoji.id!!, emoji))
        }
    }

    private suspend fun handle(event: GuildCreate, shard: Int) {
        val data = GuildData.from(event.guild)
        cache.put(data)
        event.guild.cache()

        coreFlow.emit(GuildCreateEvent(Guild(data, kord), shard))
    }

    private suspend fun handle(event: GuildUpdate, shard: Int) {
        val data = GuildData.from(event.guild)
        cache.put(data)
        event.guild.cache()

        coreFlow.emit(GuildCreateEvent(Guild(data, kord), shard))
    }

    private suspend fun handle(event: GuildDelete, shard: Int) = with(event.guild) {
        val query = cache.query<GuildData> { idEq(GuildData::id, id) }

        val old = query.asFlow().map { Guild(it, kord) }.singleOrNull()
        query.remove()

        coreFlow.emit(GuildDeleteEvent(id, unavailable.orElse(false), old, kord, shard))
    }

    private suspend fun handle(event: GuildBanAdd, shard: Int) = with(event.ban) {
        val data = UserData.from(user)
        cache.put(user)
        val user = User(data, kord)

        coreFlow.emit(BanAddEvent(user, Snowflake(guildId), shard))
    }

    private suspend fun handle(event: GuildBanRemove, shard: Int) = with(event.ban) {
        val data = UserData.from(user)
        cache.put(user)
        val user = User(data, kord)

        coreFlow.emit(BanRemoveEvent(user, Snowflake(guildId), shard))
    }

    private suspend fun handle(event: GuildEmojisUpdate, shard: Int) = with(event.emoji) {
        val data = emojis.map { EmojiData.from(guildId, it.id!!, it) }
        cache.putAll(data)

        val emojis = data.map { GuildEmoji(it, kord) }

        cache.query<GuildData> { idEq(GuildData::id, guildId) }.update {
            it.copy(emojis = emojis.map { emoji -> emoji.id })
        }

        coreFlow.emit(EmojisUpdateEvent(guildId, emojis.toSet(), kord, shard))
    }


    private suspend fun handle(event: GuildIntegrationsUpdate, shard: Int) {
        coreFlow.emit(IntegrationsUpdateEvent(event.integrations.guildId, kord, shard))
    }

    private suspend fun handle(event: GuildMemberAdd, shard: Int) = with(event.member) {
        val userData = UserData.from(user.value!!)
        val memberData = MemberData.from(user.value!!.id, event.member)

        cache.put(userData)
        cache.put(memberData)

        val member = Member(memberData, userData, kord)

        coreFlow.emit(MemberJoinEvent(member, shard))
    }

    private suspend fun handle(event: GuildMemberRemove, shard: Int) = with(event.member) {
        val userData = UserData.from(user)
        cache.query<UserData> { idEq(UserData::id, userData.id) }.remove()
        val user = User(userData, kord)

        coreFlow.emit(MemberLeaveEvent(user, guildId, shard))
    }

    private suspend fun handle(event: GuildMemberUpdate, shard: Int) = with(event.member) {
        val userData = UserData.from(user)
        cache.put(userData)

        val query = cache.query<MemberData> {
            idEq(MemberData::userId, userData.id)
            idEq(MemberData::guildId, guildId)
        }
        val old = query.asFlow().map { Member(it, userData, kord) }.singleOrNull()

        val new = Member(MemberData.from(this), userData, kord)
        cache.put(new.memberData)

        coreFlow.emit(MemberUpdateEvent(new, old, kord, shard))
    }

    private suspend fun handle(event: GuildRoleCreate, shard: Int) {
        val data = RoleData.from(event.role)
        cache.put(data)

        coreFlow.emit(RoleCreateEvent(Role(data, kord), shard))
    }

    private suspend fun handle(event: GuildRoleUpdate, shard: Int) {
        val data = RoleData.from(event.role)
        cache.put(data)

        coreFlow.emit(RoleUpdateEvent(Role(data, kord), shard))
    }

    private suspend fun handle(event: GuildRoleDelete, shard: Int) = with(event.role) {
        val query = cache.query<RoleData> { idEq(RoleData::id, event.role.id) }

        val old = run {
            val data = query.singleOrNull() ?: return@run null
            Role(data, kord)
        }

        query.remove()

        coreFlow.emit(RoleDeleteEvent(guildId, id, old, kord, shard))
    }

    private suspend fun handle(event: GuildMembersChunk, shard: Int) = with(event.data) {
        val presences = presences.orEmpty().map { PresenceData.from(guildId, it) }
        cache.putAll(presences)

        members.map { member ->
            val memberData = MemberData.from(member.user.value!!.id, guildId, member)
            cache.put(memberData)
            val userData = UserData.from(member.user.value!!)
            cache.put(userData)
        }

        coreFlow.emit(MembersChunkEvent(MembersChunkData.from(this), kord, shard))
    }

    private suspend fun handle(event: PresenceUpdate, shard: Int) = with(event.presence) {
        val data = PresenceData.from(this.guildId.value!!, this)

        val old = cache.query<PresenceData> { idEq(PresenceData::id,  data.id) }
                .asFlow().map { Presence(it, kord) }.singleOrNull()

        cache.put(data)
        val new = Presence(data, kord)

        val user = cache
                .query<UserData> { idEq(UserData::id, event.presence.user.id) }
                .singleOrNull()
                ?.let { User(it, kord) }

        coreFlow.emit(PresenceUpdateEvent(user, this.user, guildId.value!!, old, new, shard))
    }

    private suspend fun handle(event: InviteCreate, shard: Int) = with(event) {
        val data = InviteCreateData.from(invite)

        invite.inviter.value?.let { cache.put(UserData.from(it)) }
        invite.targetUser.value?.let { cache.put(UserData.from(it)) }

        coreFlow.emit(InviteCreateEvent(data, kord, shard))
    }

    private suspend fun handle(event: InviteDelete, shard: Int) = with(event) {
        val data = InviteDeleteData.from(invite)
        coreFlow.emit(InviteDeleteEvent(data, kord, shard))
    }

}