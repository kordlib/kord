package dev.kord.rest.service

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.guild.*
import dev.kord.rest.builder.integration.IntegrationModifyBuilder
import dev.kord.rest.builder.member.MemberAddBuilder
import dev.kord.rest.builder.member.MemberModifyBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.builder.role.RoleModifyBuilder
import dev.kord.rest.builder.role.RolePositionsModifyBuilder
import dev.kord.rest.builder.scheduled_events.ScheduledEventModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.json.response.*
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend inline fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): DiscordGuild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.GuildPost) {
            body(GuildCreateRequest.serializer(), GuildCreateBuilder(name).apply(builder).toRequest())
        }
    }

    /**
     * @param withCounts whether to include the [DiscordGuild.approximateMemberCount]
     * and [DiscordGuild.approximatePresenceCount] fields, `false` by default.
     */
    public suspend fun getGuild(guildId: Snowflake, withCounts: Boolean = false): DiscordGuild = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
        parameter("with_count", withCounts.toString())
    }

    /**
     * Returns the preview of this [guildId].
     */
    public suspend fun getGuildPreview(guildId: Snowflake): DiscordGuildPreview = call(Route.GuildPreviewGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend inline fun modifyGuild(guildId: Snowflake, builder: GuildModifyBuilder.() -> Unit): DiscordGuild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.GuildPatch) {
            keys[Route.GuildId] = guildId
            val modifyBuilder = GuildModifyBuilder().apply(builder)
            body(GuildModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun deleteGuild(guildId: Snowflake): Unit = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun getGuildChannels(guildId: Snowflake): List<DiscordChannel> = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun createGuildChannel(
        guildId: Snowflake,
        channel: GuildChannelCreateRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(GuildChannelCreateRequest.serializer(), channel)
        auditLogReason(reason)
    }

    public suspend inline fun modifyGuildChannelPosition(
        guildId: Snowflake,
        builder: GuildChannelPositionModifyBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        call(Route.GuildChannelsPatch) {
            keys[Route.GuildId] = guildId
            val modifyBuilder = GuildChannelPositionModifyBuilder().apply(builder)
            body(GuildChannelPositionModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun getGuildMember(guildId: Snowflake, userId: Snowflake): DiscordGuildMember =
        call(Route.GuildMemberGet) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
        }

    public suspend fun getGuildMembers(
        guildId: Snowflake,
        after: Position.After? = null,
        limit: Int? = null,
    ): List<DiscordGuildMember> = call(Route.GuildMembersGet) {
        keys[Route.GuildId] = guildId
        after?.let { parameter(it.key, it.value) }
        limit?.let { parameter("limit", it) }
    }

    /**
     * Requests members with a username or nickname starting with [query].
     *
     * @param limit limits the maximum amount of members returned. Max `1000`, defaults to `1`.
     */
    @KordExperimental
    public suspend fun getGuildMembers(
        guildId: Snowflake,
        query: String,
        limit: Int? = null,
    ): List<DiscordGuildMember> = call(Route.GuildMembersSearchGet) {
        keys[Route.GuildId] = guildId
        parameter("query", query)
        limit?.let { parameter("limit", it) }
    }

    public suspend fun addGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        token: String,
        builder: MemberAddBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        call(Route.GuildMemberPut) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            body(GuildMemberAddRequest.serializer(), MemberAddBuilder(token).also(builder).toRequest())
        }
    }

    public suspend inline fun modifyGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        builder: MemberModifyBuilder.() -> Unit
    ): DiscordGuildMember {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.GuildMemberPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            val modifyBuilder = MemberModifyBuilder().apply(builder)
            body(GuildMemberModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun addRoleToGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String? = null,
    ): Unit = call(Route.GuildMemberRolePut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        auditLogReason(reason)
    }

    public suspend fun deleteRoleFromGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String? = null,
    ): Unit = call(Route.GuildMemberRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        auditLogReason(reason)
    }

    public suspend fun deleteGuildMember(guildId: Snowflake, userId: Snowflake, reason: String? = null): Unit =
        call(Route.GuildMemberDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            auditLogReason(reason)
        }

    public suspend fun getGuildBans(
        guildId: Snowflake,
        position: Position.BeforeOrAfter? = null,
        limit: Int? = null,
    ): List<BanResponse> = call(Route.GuildBansGet) {
        keys[Route.GuildId] = guildId

        limit?.let { parameter("limit", it) }
        position?.let { parameter(it.key, it.value) }
    }

    public suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): BanResponse = call(Route.GuildBanGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    public suspend inline fun addGuildBan(guildId: Snowflake, userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        call(Route.GuildBanPut) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            val createBuilder = BanCreateBuilder().apply(builder)
            body(GuildBanCreateRequest.serializer(), createBuilder.toRequest())
            auditLogReason(createBuilder.reason)
        }
    }

    public suspend fun deleteGuildBan(guildId: Snowflake, userId: Snowflake, reason: String? = null): Unit =
        call(Route.GuildBanDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            auditLogReason(reason)
        }

    public suspend fun getGuildRoles(guildId: Snowflake): List<DiscordRole> = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend inline fun createGuildRole(
        guildId: Snowflake,
        builder: RoleCreateBuilder.() -> Unit = {},
    ): DiscordRole {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.GuildRolePost) {
            keys[Route.GuildId] = guildId
            val createBuilder = RoleCreateBuilder().apply(builder)
            body(GuildRoleCreateRequest.serializer(), createBuilder.toRequest())
            auditLogReason(createBuilder.reason)
        }
    }

    public suspend inline fun modifyGuildRolePosition(
        guildId: Snowflake,
        builder: RolePositionsModifyBuilder.() -> Unit
    ): List<DiscordRole> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return call(Route.GuildRolesPatch) {
            keys[Route.GuildId] = guildId
            val modifyBuilder = RolePositionsModifyBuilder().apply(builder)
            body(GuildRolePositionModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend inline fun modifyGuildRole(
        guildId: Snowflake,
        roleId: Snowflake,
        builder: RoleModifyBuilder.() -> Unit
    ): DiscordRole {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return call(Route.GuildRolePatch) {
            keys[Route.GuildId] = guildId
            keys[Route.RoleId] = roleId
            val modifyBuilder = RoleModifyBuilder().apply(builder)
            body(GuildRoleModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun deleteGuildRole(guildId: Snowflake, roleId: Snowflake, reason: String? = null): Unit =
        call(Route.GuildRoleDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.RoleId] = roleId
            auditLogReason(reason)
        }

    public suspend fun getGuildPruneCount(guildId: Snowflake, days: Int = 7): GetPruneResponse =
        call(Route.GuildPruneCountGet) {
            keys[Route.GuildId] = guildId
            parameter("days", days)
        }

    public suspend fun beginGuildPrune(
        guildId: Snowflake,
        days: Int = 7,
        computePruneCount: Boolean = true,
        reason: String? = null,
    ): PruneResponse = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
        parameter("compute_prune_count", computePruneCount)
        auditLogReason(reason)
    }

    public suspend fun getGuildVoiceRegions(guildId: Snowflake): List<DiscordVoiceRegion> =
        call(Route.GuildVoiceRegionsGet) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun getGuildInvites(guildId: Snowflake): List<DiscordInviteWithMetadata> =
        call(Route.GuildInvitesGet) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun getGuildIntegrations(guildId: Snowflake): List<DiscordIntegration> =
        call(Route.GuildIntegrationGet) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun createGuildIntegration(guildId: Snowflake, integration: GuildIntegrationCreateRequest): Unit =
        call(Route.GuildIntegrationPost) {
            keys[Route.GuildId] = guildId
            body(GuildIntegrationCreateRequest.serializer(), integration)
        }

    public suspend inline fun modifyGuildIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        builder: IntegrationModifyBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        call(Route.GuildIntegrationPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.IntegrationId] = integrationId
            val modifyBuilder = IntegrationModifyBuilder().apply(builder)
            body(GuildIntegrationModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun deleteGuildIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        reason: String? = null,
    ): Unit = call(Route.GuildIntegrationDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
        auditLogReason(reason)
    }

    public suspend fun syncGuildIntegration(guildId: Snowflake, integrationId: Snowflake): Unit =
        call(Route.GuildIntegrationSyncPost) {
            keys[Route.GuildId] = guildId
            keys[Route.IntegrationId] = integrationId
        }

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("getGuildWidget(guildId)"), DeprecationLevel.ERROR)
    public suspend fun getGuildEmbed(guildId: Snowflake): Nothing =
        throw Exception("Guild embeds were renamed to widgets.")

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "Guild embeds were renamed to widgets.",
        ReplaceWith("modifyGuildWidget(guildId, embed)"),
        DeprecationLevel.ERROR
    )
    public suspend fun modifyGuildEmbed(guildId: Snowflake, embed: Any): Nothing =
        throw Exception("Guild embeds were renamed to widgets.")

    public suspend fun getGuildWidget(guildId: Snowflake): DiscordGuildWidget = call(Route.GuildWidgetGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun modifyGuildWidget(
        guildId: Snowflake,
        widget: GuildWidgetModifyRequest,
        reason: String? = null,
    ): DiscordGuildWidget = call(Route.GuildWidgetPatch) {
        keys[Route.GuildId] = guildId
        body(GuildWidgetModifyRequest.serializer(), widget)
        auditLogReason(reason)
    }

    public suspend inline fun modifyGuildWidget(
        guildId: Snowflake,
        builder: GuildWidgetModifyBuilder.() -> Unit
    ): DiscordGuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val modifyBuilder = GuildWidgetModifyBuilder().apply(builder)
        return modifyGuildWidget(guildId, modifyBuilder.toRequest(), modifyBuilder.reason)
    }

    public suspend fun getVanityInvite(guildId: Snowflake): DiscordPartialInvite = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun modifyCurrentUserNickname(
        guildId: Snowflake,
        nick: CurrentUserNicknameModifyRequest,
        reason: String? = null,
    ): CurrentUserNicknameModifyResponse = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(CurrentUserNicknameModifyRequest.serializer(), nick)
        auditLogReason(reason)
    }

    public suspend fun getGuildWelcomeScreen(guildId: Snowflake): DiscordWelcomeScreen =
        call(Route.GuildWelcomeScreenGet) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun modifyGuildWelcomeScreen(
        guildId: Snowflake,
        request: GuildWelcomeScreenModifyRequest,
        reason: String? = null,
    ): DiscordWelcomeScreen = call(Route.GuildWelcomeScreenPatch) {
        keys[Route.GuildId] = guildId
        body(GuildWelcomeScreenModifyRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun modifyCurrentVoiceState(guildId: Snowflake, request: CurrentVoiceStateModifyRequest): Unit =
        call(Route.SelfVoiceStatePatch) {
            keys[Route.GuildId] = guildId
            body(CurrentVoiceStateModifyRequest.serializer(), request)
        }

    public suspend fun modifyVoiceState(guildId: Snowflake, userId: Snowflake, request: VoiceStateModifyRequest): Unit =
        call(Route.OthersVoiceStatePatch) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            body(VoiceStateModifyRequest.serializer(), request)
        }

    public suspend fun listActiveThreads(guildId: Snowflake): ListThreadsResponse = call(Route.ActiveThreadsGet) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun listScheduledEvents(
        guildId: Snowflake,
        withUserCount: Boolean? = null,
    ): List<DiscordGuildScheduledEvent> = call(Route.GuildScheduledEventsGet) {
        keys[Route.GuildId] = guildId
        if (withUserCount != null) {
            parameter("with_user_count", withUserCount)
        }
    }

    public suspend fun getScheduledEvent(guildId: Snowflake, eventId: Snowflake): DiscordGuildScheduledEvent =
        call(Route.GuildScheduledEventGet) {
            keys[Route.GuildId] = guildId
            keys[Route.ScheduledEventId] = eventId
        }

    public suspend fun createScheduledEvent(
        guildId: Snowflake,
        request: GuildScheduledEventCreateRequest,
        reason: String? = null,
    ): DiscordGuildScheduledEvent = call(Route.GuildScheduledEventsPost) {
        keys[Route.GuildId] = guildId
        auditLogReason(reason)
        body(GuildScheduledEventCreateRequest.serializer(), request)
    }

    public suspend fun modifyScheduledEvent(
        guildId: Snowflake,
        eventId: Snowflake,
        request: ScheduledEventModifyRequest,
        reason: String? = null,
    ): DiscordGuildScheduledEvent = call(Route.GuildScheduledEventPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.ScheduledEventId] = eventId
        auditLogReason(reason)
        body(ScheduledEventModifyRequest.serializer(), request)
    }

    public suspend fun deleteScheduledEvent(guildId: Snowflake, eventId: Snowflake): Unit =
        call(Route.GuildScheduledEventDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.ScheduledEventId] = eventId
        }

    public suspend fun getScheduledEventUsers(
        guildId: Snowflake,
        eventId: Snowflake,
        position: Position.BeforeOrAfter? = null,
        withMember: Boolean? = null,
        limit: Int? = null,
    ): List<GuildScheduledEventUsersResponse> = call(Route.GuildScheduledEventUsersGet) {
        keys[Route.GuildId] = guildId
        keys[Route.ScheduledEventId] = eventId

        limit?.let { parameter("limit", it) }
        withMember?.let { parameter("with_member", it) }
        position?.let { parameter(it.key, it.value) }
    }

    public suspend fun getScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        withMember: Boolean? = null,
        limit: Int? = null,
    ): List<GuildScheduledEventUsersResponse> = getScheduledEventUsers(
        guildId,
        eventId,
        Position.Before(before),
        withMember,
        limit,
    )

    public suspend fun getScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        withMember: Boolean? = null,
        limit: Int? = null,
    ): List<GuildScheduledEventUsersResponse> = getScheduledEventUsers(
        guildId,
        eventId,
        Position.After(after),
        withMember,
        limit,
    )
}

public suspend inline fun GuildService.modifyGuildWelcomeScreen(
    guildId: Snowflake,
    builder: WelcomeScreenModifyBuilder.() -> Unit
): DiscordWelcomeScreen {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifiedBuilder = WelcomeScreenModifyBuilder().apply(builder)
    return modifyGuildWelcomeScreen(guildId, modifiedBuilder.toRequest(), modifiedBuilder.reason)
}

public suspend inline fun GuildService.createTextChannel(
    guildId: Snowflake,
    name: String,
    builder: TextChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = TextChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

public suspend inline fun GuildService.createNewsChannel(
    guildId: Snowflake,
    name: String,
    builder: NewsChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = NewsChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

public suspend inline fun GuildService.createVoiceChannel(
    guildId: Snowflake,
    name: String,
    builder: VoiceChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = VoiceChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

public suspend inline fun GuildService.createCategory(
    guildId: Snowflake,
    name: String,
    builder: CategoryCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = CategoryCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

public suspend inline fun GuildService.modifyCurrentVoiceState(
    guildId: Snowflake,
    channelId: Snowflake,
    builder: CurrentVoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifyBuilder = CurrentVoiceStateModifyBuilder(channelId).apply(builder)
    modifyCurrentVoiceState(guildId, modifyBuilder.toRequest())
}

public suspend inline fun GuildService.modifyVoiceState(
    guildId: Snowflake,
    channelId: Snowflake,
    userId: Snowflake,
    builder: VoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifyBuilder = VoiceStateModifyBuilder(channelId).apply(builder)
    modifyVoiceState(guildId, userId, modifyBuilder.toRequest())
}

public suspend inline fun GuildService.createScheduledEvent(
    guildId: Snowflake,
    name: String,
    privacyLevel: GuildScheduledEventPrivacyLevel,
    scheduledStartTime: Instant,
    entityType: ScheduledEntityType,
    builder: ScheduledEventCreateBuilder.() -> Unit = {}
): DiscordGuildScheduledEvent {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val appliedBuilder = ScheduledEventCreateBuilder(
        name,
        privacyLevel,
        scheduledStartTime,
        entityType
    ).apply(builder)

    return createScheduledEvent(guildId, appliedBuilder.toRequest(), appliedBuilder.reason)
}

public suspend inline fun GuildService.modifyScheduledEvent(
    guildId: Snowflake,
    eventId: Snowflake,
    builder: ScheduledEventModifyBuilder.() -> Unit
): DiscordGuildScheduledEvent {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val appliedBuilder = ScheduledEventModifyBuilder().apply(builder)

    return modifyScheduledEvent(guildId, eventId, appliedBuilder.toRequest(), appliedBuilder.reason)
}
