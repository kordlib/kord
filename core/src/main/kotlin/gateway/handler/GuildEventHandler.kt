package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.putAll
import dev.kord.cache.api.query
import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordPartialGuild
import dev.kord.common.entity.optional.Optional
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
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import dev.kord.common.entity.DiscordGuild as GatewayGuild
import dev.kord.core.event.Event as CoreEvent

internal class GuildEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is GuildCreate -> handle(event, shard, kord, coroutineScope)
            is GuildUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildDelete -> handle(event, shard, kord, coroutineScope)
            is GuildBanAdd -> handle(event, shard, kord, coroutineScope)
            is GuildBanRemove -> handle(event, shard, kord, coroutineScope)
            is GuildEmojisUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildIntegrationsUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildMemberAdd -> handle(event, shard, kord, coroutineScope)
            is GuildMemberRemove -> handle(event, shard, kord, coroutineScope)
            is GuildMemberUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildRoleCreate -> handle(event, shard, kord, coroutineScope)
            is GuildRoleUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildRoleDelete -> handle(event, shard, kord, coroutineScope)
            is GuildMembersChunk -> handle(event, shard, kord, coroutineScope)
            is GuildScheduledEventCreate -> handle(event, shard, kord, coroutineScope)
            is GuildScheduledEventUpdate -> handle(event, shard, kord, coroutineScope)
            is GuildScheduledEventDelete -> handle(event, shard, kord, coroutineScope)
            is GuildScheduledEventUserAdd -> handle(event, shard, kord, coroutineScope)
            is GuildScheduledEventUserRemove -> handle(event, shard, kord, coroutineScope)
            is PresenceUpdate -> handle(event, shard, kord, coroutineScope)
            is InviteCreate -> handle(event, shard, kord, coroutineScope)
            is InviteDelete -> handle(event, shard, kord, coroutineScope)
            else -> null
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

        for (thread in threads.orEmpty()) {
            cache.put(ChannelData.from(thread.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
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
    private suspend fun DiscordPartialGuild.cache() {
        for (member in members.orEmpty()) {
            cache.put(MemberData.from(member.user.value!!.id, id, member))
            cache.put(UserData.from(member.user.value!!))
        }

        for (role in roles.orEmpty()) {
            cache.put(RoleData.from(id, role))
        }

        for (channel in channels.orEmpty()) {
            cache.put(ChannelData.from(channel.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
        }

        for (thread in threads.orEmpty()) {
            cache.put(ChannelData.from(thread.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
        }

        for (presence in presences.orEmpty()) {
            cache.put(PresenceData.from(id, presence))
        }

        for (voiceState in voiceStates.orEmpty()) {
            cache.put(VoiceStateData.from(id, voiceState))
        }

        for (emoji in emojis.orEmpty()) {
            cache.put(EmojiData.from(id, emoji.id!!, emoji))
        }
    }

    private suspend fun handle(
        event: GuildCreate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        if (event.guild.name is Optional.Missing<*>) {
            return UnavailableGuildCreateEvent(event.guild.id, shard, kord)
        }
        val data = GuildData.from(event.guild)
        cache.put(data)
        event.guild.cache()

        return GuildCreateEvent(Guild(data, kord), shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): GuildUpdateEvent {
        val data = GuildData.from(event.guild)
        val old = cache.query<GuildData> { idEq(GuildData::id, event.guild.id) }.singleOrNull()
        cache.put(data)
        event.guild.cache()

        return GuildUpdateEvent(Guild(data, kord), old?.let { Guild(it, kord) }, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildDelete,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): GuildDeleteEvent = with(event.guild) {
        val query = cache.query<GuildData> { idEq(GuildData::id, id) }

        val old = query.asFlow().map { Guild(it, kord) }.singleOrNull()
        query.remove()

        return GuildDeleteEvent(id, unavailable.orElse(false), old, kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildBanAdd,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): BanAddEvent =
        with(event.ban) {
            val data = UserData.from(user)
            cache.put(user)
            val user = User(data, kord)

            return BanAddEvent(user, guildId, shard, coroutineScope = coroutineScope)
        }

    private suspend fun handle(
        event: GuildBanRemove,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): BanRemoveEvent = with(event.ban) {
        val data = UserData.from(user)
        cache.put(user)
        val user = User(data, kord)

        return BanRemoveEvent(user, guildId, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildEmojisUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): EmojisUpdateEvent =
        with(event.emoji) {

            val data = emojis.map { EmojiData.from(guildId, it.id!!, it) }
            val old = cache.query<EmojiData> { idEq(EmojiData::guildId, guildId) }.asFlow().map {
                GuildEmoji(it, kord)
            }.toSet()
            cache.putAll(data)

            val emojis = data.map { GuildEmoji(it, kord) }

            cache.query<GuildData> { idEq(GuildData::id, guildId) }.update {
                it.copy(emojis = emojis.map { emoji -> emoji.id })
            }

            return EmojisUpdateEvent(guildId, emojis.toSet(), old, kord, shard, coroutineScope = coroutineScope)
        }


    private fun handle(
        event: GuildIntegrationsUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): IntegrationsUpdateEvent {
        return IntegrationsUpdateEvent(event.integrations.guildId, kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildMemberAdd,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): MemberJoinEvent = with(event.member) {
        val userData = UserData.from(user.value!!)
        val memberData = MemberData.from(user.value!!.id, event.member)

        cache.put(userData)
        cache.put(memberData)

        val member = Member(memberData, userData, kord)

        return MemberJoinEvent(member, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildMemberRemove,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): MemberLeaveEvent =
        with(event.member) {
            val userData = UserData.from(user)

            val oldData = cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }.singleOrNull()

            cache.query<UserData> { idEq(UserData::id, userData.id) }.remove()

            val user = User(userData, kord)
            val old = oldData?.let { Member(it, userData, kord) }

            return MemberLeaveEvent(user, old, guildId, shard, coroutineScope = coroutineScope)
        }

    private suspend fun handle(
        event: GuildMemberUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): MemberUpdateEvent =
        with(event.member) {
            val userData = UserData.from(user)
            cache.put(userData)

            val query = cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }
            val old = query.asFlow().map { Member(it, userData, kord) }.singleOrNull()

            val new = Member(MemberData.from(this), userData, kord)
            cache.put(new.memberData)

            return MemberUpdateEvent(new, old, kord, shard, coroutineScope = coroutineScope)
        }

    private suspend fun handle(
        event: GuildRoleCreate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): RoleCreateEvent {
        val data = RoleData.from(event.role)
        cache.put(data)

        return RoleCreateEvent(Role(data, kord), shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildRoleUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): RoleUpdateEvent {
        val data = RoleData.from(event.role)

        val oldData = cache.query<RoleData> {
            idEq(RoleData::id, data.id)
            idEq(RoleData::guildId, data.guildId) // TODO("Is this worth keeping?")
        }.singleOrNull()

        val old = oldData?.let { Role(it, kord) }
        cache.put(data)

        return RoleUpdateEvent(Role(data, kord), old, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildRoleDelete,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): RoleDeleteEvent = with(event.role) {
        val query = cache.query<RoleData> { idEq(RoleData::id, event.role.id) }

        val old = run {
            val data = query.singleOrNull() ?: return@run null
            Role(data, kord)
        }

        query.remove()

        return RoleDeleteEvent(guildId, id, old, kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildMembersChunk,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): MembersChunkEvent = with(event.data) {
        val presences = presences.orEmpty().map { PresenceData.from(guildId, it) }
        cache.putAll(presences)

        members.map { member ->
            val memberData = MemberData.from(member.user.value!!.id, guildId, member)
            cache.put(memberData)
            val userData = UserData.from(member.user.value!!)
            cache.put(userData)
        }

        return MembersChunkEvent(MembersChunkData.from(this), kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildScheduledEventCreate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): GuildScheduledEventCreateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventCreateEvent(scheduledEvent, kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildScheduledEventUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): GuildScheduledEventUpdateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        val oldData = cache.query<GuildScheduledEventData> {
            idEq(GuildScheduledEventData::id, event.event.id)
        }.singleOrNull()
        val old = oldData?.let { GuildScheduledEvent(it, kord) }
        cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventUpdateEvent(scheduledEvent, old, kord, shard, coroutineScope = coroutineScope)
    }

    private suspend fun handle(
        event: GuildScheduledEventDelete,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): GuildScheduledEventDeleteEvent {
        val query = cache.query<GuildScheduledEvent> {
            idEq(GuildScheduledEvent::id, event.event.id)
        }
        query.remove()

        val eventData = GuildScheduledEventData.from(event.event)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventDeleteEvent(scheduledEvent, kord, shard, coroutineScope = coroutineScope)
    }

    private fun handle(
        event: GuildScheduledEventUserAdd,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope,
    ): GuildScheduledEventUserAddEvent = with(event.data) {
        GuildScheduledEventUserAddEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            shard,
            coroutineScope = coroutineScope,
        )
    }

    private fun handle(
        event: GuildScheduledEventUserRemove,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope,
    ): GuildScheduledEventUserRemoveEvent = with(event.data) {
        GuildScheduledEventUserRemoveEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            shard,
            coroutineScope = coroutineScope,
        )
    }

    private suspend fun handle(
        event: PresenceUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): PresenceUpdateEvent =
        with(event.presence) {
            val data = PresenceData.from(this.guildId.value!!, this)

            val old = cache.query<PresenceData> { idEq(PresenceData::id, data.id) }
                .asFlow().map { Presence(it, kord) }.singleOrNull()

            cache.put(data)
            val new = Presence(data, kord)

            val user = cache
                .query<UserData> { idEq(UserData::id, event.presence.user.id) }
                .singleOrNull()
                ?.let { User(it, kord) }

            return PresenceUpdateEvent(
                user,
                this.user,
                guildId.value!!,
                old,
                new,
                shard,
                coroutineScope = coroutineScope
            )
        }

    private suspend fun handle(
        event: InviteCreate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): InviteCreateEvent = with(event) {
        val data = InviteCreateData.from(invite)

        invite.inviter.value?.let { cache.put(UserData.from(it)) }
        invite.targetUser.value?.let { cache.put(UserData.from(it)) }

        return InviteCreateEvent(data, kord, shard, coroutineScope = coroutineScope)
    }

    private fun handle(
        event: InviteDelete,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): InviteDeleteEvent = with(event) {
        val data = InviteDeleteData.from(invite)
        return InviteDeleteEvent(data, kord, shard, coroutineScope = coroutineScope)
    }
}
