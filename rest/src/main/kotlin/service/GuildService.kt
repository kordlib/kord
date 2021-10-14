package dev.kord.rest.service

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.DiscordGuildWidget
import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.DiscordWelcomeScreen
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.channel.CategoryCreateBuilder
import dev.kord.rest.builder.channel.GuildChannelPositionModifyBuilder
import dev.kord.rest.builder.channel.NewsChannelCreateBuilder
import dev.kord.rest.builder.channel.TextChannelCreateBuilder
import dev.kord.rest.builder.channel.VoiceChannelCreateBuilder
import dev.kord.rest.builder.guild.CurrentVoiceStateModifyBuilder
import dev.kord.rest.builder.guild.GuildCreateBuilder
import dev.kord.rest.builder.guild.GuildModifyBuilder
import dev.kord.rest.builder.guild.GuildWidgetModifyBuilder
import dev.kord.rest.builder.guild.ScheduledEventCreateBuilder
import dev.kord.rest.builder.guild.VoiceStateModifyBuilder
import dev.kord.rest.builder.guild.WelcomeScreenModifyBuilder
import dev.kord.rest.builder.integration.IntegrationModifyBuilder
import dev.kord.rest.builder.member.MemberAddBuilder
import dev.kord.rest.builder.member.MemberModifyBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.builder.role.RoleModifyBuilder
import dev.kord.rest.builder.role.RolePositionsModifyBuilder
import dev.kord.rest.json.request.CurrentUserNicknameModifyRequest
import dev.kord.rest.json.request.CurrentVoiceStateModifyRequest
import dev.kord.rest.json.request.GuildBanCreateRequest
import dev.kord.rest.json.request.GuildChannelCreateRequest
import dev.kord.rest.json.request.GuildChannelPositionModifyRequest
import dev.kord.rest.json.request.GuildCreateRequest
import dev.kord.rest.json.request.GuildIntegrationCreateRequest
import dev.kord.rest.json.request.GuildIntegrationModifyRequest
import dev.kord.rest.json.request.GuildMemberAddRequest
import dev.kord.rest.json.request.GuildMemberModifyRequest
import dev.kord.rest.json.request.GuildModifyRequest
import dev.kord.rest.json.request.GuildRoleCreateRequest
import dev.kord.rest.json.request.GuildRoleModifyRequest
import dev.kord.rest.json.request.GuildRolePositionModifyRequest
import dev.kord.rest.json.request.GuildScheduledEventCreateRequest
import dev.kord.rest.json.request.GuildWelcomeScreenModifyRequest
import dev.kord.rest.json.request.GuildWidgetModifyRequest
import dev.kord.rest.json.request.VoiceStateModifyRequest
import dev.kord.rest.json.response.ListThreadsResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import kotlinx.datetime.Instant
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): DiscordGuild {
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
    suspend fun getGuild(guildId: Snowflake, withCounts: Boolean = false) = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
        parameter("with_count", withCounts.toString())
    }

    /**
     * Returns the preview of this [guildId].
     */
    suspend fun getGuildPreview(guildId: Snowflake) = call(Route.GuildPreviewGet) {
        keys[Route.GuildId] = guildId
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuild(guildId: Snowflake, builder: GuildModifyBuilder.() -> Unit): DiscordGuild {
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

    suspend fun deleteGuild(guildId: Snowflake) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: Snowflake) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: Snowflake, channel: GuildChannelCreateRequest, reason: String? = null) =
        call(Route.GuildChannelsPost) {
            keys[Route.GuildId] = guildId
            body(GuildChannelCreateRequest.serializer(), channel)
            auditLogReason(reason)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildChannelPosition(
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

    suspend fun getGuildMember(guildId: Snowflake, userId: Snowflake) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: Snowflake, position: Position? = null, limit: Int = 1) =
        call(Route.GuildMembersGet) {
            keys[Route.GuildId] = guildId
            if (position != null) {
                parameter(position.key, position.value)
            }
            parameter("limit", "$limit")
        }

    /**
     * Requests members with a username or nickname matching [query].
     *
     * @param limit limits the maximum amount of members returned. Max `1000`, defaults to `1`.
     */
    @KordExperimental
    suspend fun getGuildMembers(guildId: Snowflake, query: String, limit: Int = 1) = call(Route.GuildMembersSearchGet) {
        keys[Route.GuildId] = guildId
        parameter("query", query)
        parameter("limit", "$limit")
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun addGuildMember(
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

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildMember(
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

    suspend fun addRoleToGuildMember(guildId: Snowflake, userId: Snowflake, roleId: Snowflake, reason: String? = null) =
        call(Route.GuildMemberRolePut) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            keys[Route.RoleId] = roleId
            auditLogReason(reason)
        }

    suspend fun deleteRoleFromGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String? = null
    ) = call(Route.GuildMemberRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        auditLogReason(reason)
    }

    suspend fun deleteGuildMember(guildId: Snowflake, userId: Snowflake, reason: String? = null) =
        call(Route.GuildMemberDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            auditLogReason(reason)
        }

    suspend fun getGuildBans(guildId: Snowflake) = call(Route.GuildBansGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake) = call(Route.GuildBanGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun addGuildBan(guildId: Snowflake, userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
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

    suspend fun deleteGuildBan(guildId: Snowflake, userId: Snowflake, reason: String? = null) =
        call(Route.GuildBanDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            auditLogReason(reason)
        }

    suspend fun getGuildRoles(guildId: Snowflake) = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildRole(guildId: Snowflake, builder: RoleCreateBuilder.() -> Unit = {}): DiscordRole {
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

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildRolePosition(
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

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildRole(
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

    suspend fun deleteGuildRole(guildId: Snowflake, roleId: Snowflake, reason: String? = null) =
        call(Route.GuildRoleDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.RoleId] = roleId
            auditLogReason(reason)
        }

    suspend fun getGuildPruneCount(guildId: Snowflake, days: Int = 7) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
    }

    suspend fun beginGuildPrune(
        guildId: Snowflake,
        days: Int = 7,
        computePruneCount: Boolean = true,
        reason: String? = null
    ) = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
        parameter("compute_prune_count", computePruneCount)
        auditLogReason(reason)
    }

    suspend fun getGuildVoiceRegions(guildId: Snowflake) = call(Route.GuildVoiceRegionsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildInvites(guildId: Snowflake) = call(Route.GuildInvitesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildIntegrations(guildId: Snowflake) = call(Route.GuildIntegrationGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildIntegration(guildId: Snowflake, integration: GuildIntegrationCreateRequest) =
        call(Route.GuildIntegrationPost) {
            keys[Route.GuildId] = guildId
            body(GuildIntegrationCreateRequest.serializer(), integration)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildIntegration(
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

    suspend fun deleteGuildIntegration(guildId: Snowflake, integrationId: Snowflake, reason: String? = null) =
        call(Route.GuildIntegrationDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.IntegrationId] = integrationId
            auditLogReason(reason)
        }

    suspend fun syncGuildIntegration(guildId: Snowflake, integrationId: Snowflake) =
        call(Route.GuildIntegrationSyncPost) {
            keys[Route.GuildId] = guildId
            keys[Route.IntegrationId] = integrationId
        }

    @Suppress("RedundantSuspendModifier")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("getGuildWidget(guildId)"), DeprecationLevel.ERROR)
    suspend fun getGuildEmbed(guildId: Snowflake): Nothing = throw Exception("Guild embeds were renamed to widgets.")

    @Suppress("RedundantSuspendModifier")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "Guild embeds were renamed to widgets.",
        ReplaceWith("modifyGuildWidget(guildId, embed)"),
        DeprecationLevel.ERROR
    )
    suspend fun modifyGuildEmbed(guildId: Snowflake, embed: Any): Nothing =
        throw Exception("Guild embeds were renamed to widgets.")

    suspend fun getGuildWidget(guildId: Snowflake): DiscordGuildWidget = call(Route.GuildWidgetGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuildWidget(
        guildId: Snowflake,
        widget: GuildWidgetModifyRequest,
        reason: String? = null
    ): DiscordGuildWidget =
        call(Route.GuildWidgetPatch) {
            keys[Route.GuildId] = guildId
            body(GuildWidgetModifyRequest.serializer(), widget)
            auditLogReason(reason)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildWidget(
        guildId: Snowflake,
        builder: GuildWidgetModifyBuilder.() -> Unit
    ): DiscordGuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val modifyBuilder = GuildWidgetModifyBuilder().apply(builder)
        return modifyGuildWidget(guildId, modifyBuilder.toRequest(), modifyBuilder.reason)
    }

    suspend fun getVanityInvite(guildId: Snowflake) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(
        guildId: Snowflake,
        nick: CurrentUserNicknameModifyRequest,
        reason: String? = null
    ) =
        call(Route.GuildCurrentUserNickPatch) {
            keys[Route.GuildId] = guildId
            body(CurrentUserNicknameModifyRequest.serializer(), nick)
            auditLogReason(reason)
        }

    suspend fun getGuildWelcomeScreen(guildId: Snowflake) = call(Route.GuildWelcomeScreenGet) {
        keys[Route.GuildId] = guildId
    }


    suspend fun modifyGuildWelcomeScreen(
        guildId: Snowflake,
        request: GuildWelcomeScreenModifyRequest,
        reason: String? = null
    ) =
        call(Route.GuildWelcomeScreenPatch) {
            keys[Route.GuildId] = guildId
            body(GuildWelcomeScreenModifyRequest.serializer(), request)
            auditLogReason(reason)
        }


    suspend fun modifyCurrentVoiceState(guildId: Snowflake, request: CurrentVoiceStateModifyRequest) =
        call(Route.SelfVoiceStatePatch) {
            keys[Route.GuildId] = guildId
            body(CurrentVoiceStateModifyRequest.serializer(), request)
        }


    suspend fun modifyVoiceState(guildId: Snowflake, userId: Snowflake, request: VoiceStateModifyRequest) =
        call(Route.OthersVoiceStatePatch) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            body(VoiceStateModifyRequest.serializer(), request)
        }


    suspend fun listActiveThreads(guildId: Snowflake): ListThreadsResponse {
        return call(Route.ActiveThreadsGet) {
            keys[Route.GuildId] = guildId
        }
    }

    suspend fun listScheduledEvents(
        guildId: Snowflake,
        withUserCount: Boolean? = null
    ): List<DiscordGuildScheduledEvent> =
        call(Route.GuildScheduledEventsGet) {
            keys[Route.GuildId] = guildId
            if (withUserCount != null) {
                parameter("with_user_count", withUserCount)
            }
        }

    suspend fun createScheduledEvent(
        guildId: Snowflake,
        request: GuildScheduledEventCreateRequest
    ) = call(Route.GuildScheduledEventsPost) {
        keys[Route.GuildId] = guildId

        body(GuildScheduledEventCreateRequest.serializer(), request)
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun createScheduledEvent(
        guildId: Snowflake,
        name: String,
        privacyLevel: StageInstancePrivacyLevel,
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

        return createScheduledEvent(guildId, appliedBuilder.toRequest())
    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.modifyGuildWelcomeScreen(
    guildId: Snowflake,
    builder: WelcomeScreenModifyBuilder.() -> Unit
): DiscordWelcomeScreen {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = WelcomeScreenModifyBuilder().apply(builder)
    return modifyGuildWelcomeScreen(guildId, builder.toRequest(), builder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createTextChannel(
    guildId: Snowflake,
    name: String,
    builder: TextChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = TextChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createNewsChannel(
    guildId: Snowflake,
    name: String,
    builder: NewsChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = NewsChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createVoiceChannel(
    guildId: Snowflake,
    name: String,
    builder: VoiceChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = VoiceChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createCategory(
    guildId: Snowflake,
    name: String,
    builder: CategoryCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = CategoryCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}


@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.modifyCurrentVoiceState(
    guildId: Snowflake,
    channelId: Snowflake,
    builder: CurrentVoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifyBuilder = CurrentVoiceStateModifyBuilder(channelId).apply(builder)
    modifyCurrentVoiceState(guildId, modifyBuilder.toRequest())
}


@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.modifyVoiceState(
    guildId: Snowflake,
    channelId: Snowflake,
    userId: Snowflake,
    builder: VoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifyBuilder = VoiceStateModifyBuilder(channelId).apply(builder)
    modifyVoiceState(guildId, userId, modifyBuilder.toRequest())
}
