package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.putAll
import dev.kord.cache.api.query
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import dev.kord.common.entity.DiscordGuild as GatewayGuild
import dev.kord.core.event.Event as CoreEvent

internal class GuildEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is GuildAuditLogEntryCreate -> handle(event, shard, kord, context)
            is GuildCreate -> handle(event, shard, kord, context)
            is GuildUpdate -> handle(event, shard, kord, context)
            is GuildDelete -> handle(event, shard, kord, context)
            is GuildBanAdd -> handle(event, shard, kord, context)
            is GuildBanRemove -> handle(event, shard, kord, context)
            is GuildEmojisUpdate -> handle(event, shard, kord, context)
            is GuildIntegrationCreate -> handle(event, shard, kord, context)
            is GuildIntegrationUpdate -> handle(event, shard, kord, context)
            is GuildIntegrationDelete -> handle(event, shard, kord, context)
            is GuildMemberAdd -> handle(event, shard, kord, context)
            is GuildMemberRemove -> handle(event, shard, kord, context)
            is GuildMemberUpdate -> handle(event, shard, kord, context)
            is GuildRoleCreate -> handle(event, shard, kord, context)
            is GuildRoleUpdate -> handle(event, shard, kord, context)
            is GuildRoleDelete -> handle(event, shard, kord, context)
            is GuildMembersChunk -> handle(event, shard, kord, context)
            is GuildScheduledEventCreate -> handle(event, shard, kord, context)
            is GuildScheduledEventUpdate -> handle(event, shard, kord, context)
            is GuildScheduledEventDelete -> handle(event, shard, kord, context)
            is GuildScheduledEventUserAdd -> handle(event, shard, kord, context)
            is GuildScheduledEventUserRemove -> handle(event, shard, kord, context)
            is PresenceUpdate -> handle(event, shard, kord, context)
            is InviteCreate -> handle(event, shard, kord, context)
            is InviteDelete -> handle(event, shard, kord, context)
            else -> null
        }

    private suspend fun GatewayGuild.cache(cache: DataCache) {
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

    private suspend fun handle(
        event: GuildAuditLogEntryCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?
    ): GuildAuditLogEntryCreateEvent {
        val entry = AuditLogEntry(event.entry, kord)

        kord.cache.put(entry)

        return GuildAuditLogEntryCreateEvent(entry, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildCreateEvent {
        val data = GuildData.from(event.guild)
        kord.cache.put(data)
        event.guild.cache(kord.cache)

        return GuildCreateEvent(Guild(data, kord), shard, context?.get())
    }

    private suspend fun handle(
        event: GuildUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildUpdateEvent {
        val data = GuildData.from(event.guild)
        val old = kord.cache.query<GuildData> { idEq(GuildData::id, event.guild.id) }.singleOrNull()
        kord.cache.put(data)
        event.guild.cache(kord.cache)

        return GuildUpdateEvent(Guild(data, kord), old?.let { Guild(it, kord) }, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildDeleteEvent = with(event.guild) {
        val query = kord.cache.query<GuildData> { idEq(GuildData::id, id) }

        val old = query.asFlow().map { Guild(it, kord) }.singleOrNull()
        query.remove()

        GuildDeleteEvent(id, unavailable.orElse(false), old, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildBanAdd,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): BanAddEvent =
        with(event.ban) {
            val data = UserData.from(user)
            kord.cache.put(user)
            val user = User(data, kord)

            BanAddEvent(user, guildId, shard, context?.get())
        }

    private suspend fun handle(
        event: GuildBanRemove,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): BanRemoveEvent = with(event.ban) {
        val data = UserData.from(user)
        kord.cache.put(user)
        val user = User(data, kord)

        BanRemoveEvent(user, guildId, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildEmojisUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): EmojisUpdateEvent =
        with(event.emoji) {

            val data = emojis.map { EmojiData.from(guildId, it.id!!, it) }
            val old = kord.cache.query<EmojiData> { idEq(EmojiData::guildId, guildId) }.asFlow().map {
                GuildEmoji(it, kord)
            }.toSet()
            kord.cache.putAll(data)

            val emojis = data.map { GuildEmoji(it, kord) }

            kord.cache.query<GuildData> { idEq(GuildData::id, guildId) }.update {
                it.copy(emojis = emojis.map { emoji -> emoji.id })
            }

            EmojisUpdateEvent(guildId, emojis.toSet(), old, kord, shard, context?.get())
        }

    private suspend fun handle(
        event: GuildIntegrationCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): IntegrationCreateEvent {
        return IntegrationCreateEvent(event.integration.guildId, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildIntegrationUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): IntegrationUpdateEvent {
        return IntegrationUpdateEvent(event.integration.guildId, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildIntegrationDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): IntegrationDeleteEvent {
        return IntegrationDeleteEvent(event.integration.id, event.integration.guildId, event.integration.applicationId, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildMemberAdd,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): MemberJoinEvent = with(event.member) {
        val userData = UserData.from(user.value!!)
        val memberData = MemberData.from(user.value!!.id, event.member)

        kord.cache.put(userData)
        kord.cache.put(memberData)

        val member = Member(memberData, userData, kord)

        MemberJoinEvent(member, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildMemberRemove,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): MemberLeaveEvent =
        with(event.member) {
            val userData = UserData.from(user)

            val oldData = kord.cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }.singleOrNull()

            kord.cache.query<UserData> { idEq(UserData::id, userData.id) }.remove()

            val user = User(userData, kord)
            val old = oldData?.let { Member(it, userData, kord) }

            MemberLeaveEvent(user, old, guildId, shard, context?.get())
        }

    private suspend fun handle(
        event: GuildMemberUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): MemberUpdateEvent =
        with(event.member) {
            val userData = UserData.from(user)
            kord.cache.put(userData)

            val query = kord.cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }
            val old = query.asFlow().map { Member(it, userData, kord) }.singleOrNull()

            val new = Member(MemberData.from(this), userData, kord)
            kord.cache.put(new.memberData)

            MemberUpdateEvent(new, old, kord, shard, context?.get())
        }

    private suspend fun handle(
        event: GuildRoleCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): RoleCreateEvent {
        val data = RoleData.from(event.role)
        kord.cache.put(data)

        return RoleCreateEvent(Role(data, kord), shard, context?.get())
    }

    private suspend fun handle(
        event: GuildRoleUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): RoleUpdateEvent {
        val data = RoleData.from(event.role)

        val oldData = kord.cache.query<RoleData> {
            idEq(RoleData::id, data.id)
            idEq(RoleData::guildId, data.guildId) // TODO("Is this worth keeping?")
        }.singleOrNull()

        val old = oldData?.let { Role(it, kord) }
        kord.cache.put(data)

        return RoleUpdateEvent(Role(data, kord), old, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildRoleDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): RoleDeleteEvent = with(event.role) {
        val query = kord.cache.query<RoleData> { idEq(RoleData::id, event.role.id) }

        val old = run {
            val data = query.singleOrNull() ?: return@run null
            Role(data, kord)
        }

        query.remove()

        RoleDeleteEvent(guildId, id, old, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildMembersChunk,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): MembersChunkEvent = with(event.data) {
        val presences = presences.orEmpty().map { PresenceData.from(guildId, it) }
        kord.cache.putAll(presences)

        members.map { member ->
            val memberData = MemberData.from(member.user.value!!.id, guildId, member)
            kord.cache.put(memberData)
            val userData = UserData.from(member.user.value!!)
            kord.cache.put(userData)
        }

        MembersChunkEvent(MembersChunkData.from(this), kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventCreateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        kord.cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventCreateEvent(scheduledEvent, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUpdateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        val oldData = kord.cache.query<GuildScheduledEventData> {
            idEq(GuildScheduledEventData::id, event.event.id)
        }.singleOrNull()
        val old = oldData?.let { GuildScheduledEvent(it, kord) }
        kord.cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventUpdateEvent(scheduledEvent, old, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventDeleteEvent {
        val query = kord.cache.query<GuildScheduledEvent> {
            idEq(GuildScheduledEvent::id, event.event.id)
        }
        query.remove()

        val eventData = GuildScheduledEventData.from(event.event)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventDeleteEvent(scheduledEvent, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventUserAdd,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUserAddEvent = with(event.data) {
        GuildScheduledEventUserAddEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            shard,
            context?.get(),
        )
    }

    private suspend fun handle(
        event: GuildScheduledEventUserRemove,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUserRemoveEvent = with(event.data) {
        GuildScheduledEventUserRemoveEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            shard,
            context?.get(),
        )
    }

    private suspend fun handle(
        event: PresenceUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): PresenceUpdateEvent =
        with(event.presence) {
            val data = PresenceData.from(this.guildId.value!!, this)

            val old = kord.cache.query<PresenceData> { idEq(PresenceData::id, data.id) }
                .asFlow().map { Presence(it, kord) }.singleOrNull()

            kord.cache.put(data)
            val new = Presence(data, kord)

            val user = kord.cache
                .query<UserData> { idEq(UserData::id, event.presence.user.id) }
                .singleOrNull()
                ?.let { User(it, kord) }

            PresenceUpdateEvent(
                user,
                this.user,
                guildId.value!!,
                old,
                new,
                shard,
                context?.get(),
            )
        }

    private suspend fun handle(
        event: InviteCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): InviteCreateEvent = with(event) {
        val data = InviteCreateData.from(invite)

        invite.inviter.value?.let { kord.cache.put(UserData.from(it)) }
        invite.targetUser.value?.let { kord.cache.put(UserData.from(it)) }

        InviteCreateEvent(data, kord, shard, context?.get())
    }

    private suspend fun handle(
        event: InviteDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): InviteDeleteEvent = with(event) {
        val data = InviteDeleteData.from(invite)
        InviteDeleteEvent(data, kord, shard, context?.get())
    }
}
