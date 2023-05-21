package dev.kord.rest.service

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.rest.*
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
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class GuildService(public val client: HttpClient) {

    public suspend inline fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): DiscordGuild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return client.post(Routes.Guilds) {
            setBody(GuildCreateBuilder(name).apply(builder).toRequest())
        }.body()
    }

    /**
     * @param withCounts whether to include the [DiscordGuild.approximateMemberCount]
     * and [DiscordGuild.approximatePresenceCount] fields, `false` by default.
     */
    public suspend fun getGuild(guildId: Snowflake, withCounts: Boolean = false): DiscordGuild =
        client.get(Routes.Guilds.ById(guildId)) {
        parameter("with_counts", withCounts.toString())
    }.body()

    /**
     * Returns the preview of this [guildId].
     */
    public suspend fun getGuildPreview(guildId: Snowflake): DiscordGuildPreview =
        client.get(Routes.Guilds.ById.Preview(guildId)).body()

    /** Returns the [onboarding][DiscordGuildOnboarding] object for the [guildId]. */
    public suspend fun getGuildOnboarding(guildId: Snowflake): DiscordGuildOnboarding =
        client.get(Routes.Guilds.ById.OnBoarding(guildId)).body()

    public suspend inline fun modifyGuild(guildId: Snowflake, builder: GuildModifyBuilder.() -> Unit): DiscordGuild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.patch(Routes.Guilds.ById(guildId)) {
            val modifyBuilder = GuildModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend fun deleteGuild(guildId: Snowflake) {
        client.delete(Routes.Guilds.ById(guildId))
    }

    public suspend fun getGuildChannels(guildId: Snowflake): List<DiscordChannel> =
        client.get(Routes.Guilds.ById.Channels(guildId)).body()

    public suspend fun createGuildChannel(
        guildId: Snowflake,
        request: GuildChannelCreateRequest,
        reason: String? = null,
    ): DiscordChannel = client.post(Routes.Guilds.ById.Channels(guildId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend inline fun modifyGuildChannelPosition(
        guildId: Snowflake,
        builder: GuildChannelPositionModifyBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        // TODO("Do we need this?")
        client.patch(Routes.Guilds.ById.Channels) {
            val modifyBuilder = GuildChannelPositionModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
        }
    }

    public suspend fun getGuildMember(guildId: Snowflake, userId: Snowflake): DiscordGuildMember =
        client.get(Routes.Guilds.ById.Members.ById(guildId, userId)).body()

    public suspend fun getGuildMembers(
        guildId: Snowflake,
        after: Position.After? = null,
        limit: Int? = null,
    ): List<DiscordGuildMember> =
        client.get(Routes.Guilds.ById.Members(guildId)) {
            after?.let { parameter(it.key, it.value) }
            limit?.let { parameter("limit", it) }
    }.body()

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
    ): List<DiscordGuildMember> =
        client.get(Routes.Guilds.ById.Members(guildId)) {
            parameter("query", query)
            limit?.let { parameter("limit", it) }
    }.body()

    public suspend fun addGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        token: String,
        builder: MemberAddBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        client.put(Routes.Guilds.ById.Members.ById(guildId, userId)) {
            val builder = MemberAddBuilder(token).apply(builder)
            setBody(builder.toRequest())
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

        return client.patch(Routes.Guilds.ById.Members.ById(guildId, userId)) {
            val modifyBuilder = MemberModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend fun addRoleToGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String? = null,
    ): Unit {
        client.put(Routes.Guilds.ById.Members.ById.Roles.ById(guildId, userId, roleId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun deleteRoleFromGuildMember(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String? = null,
    ) {
        client.delete(Routes.Guilds.ById.Members.ById.Roles.ById(guildId, userId, roleId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun deleteGuildMember(guildId: Snowflake, userId: Snowflake, reason: String? = null): Unit {
        client.delete(Routes.Guilds.ById.Members.ById(guildId, userId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun getGuildBans(
        guildId: Snowflake,
        position: Position.BeforeOrAfter? = null,
        limit: Int? = null,
    ): List<BanResponse> =
        client.get(Routes.Guilds.ById.Bans(guildId)) {
            limit?.let { parameter("limit", it) }
            position?.let { parameter(it.key, it.value) }
    }.body()

    public suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): BanResponse =
        client.get(Routes.Guilds.ById.Bans.ById(guildId, userId)).body()

    public suspend inline fun addGuildBan(guildId: Snowflake, userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        client.put(Routes.Guilds.ById.Bans.ById(guildId, userId)) {
            val builder = BanCreateBuilder().apply(builder)
            setBody(builder.toRequest())
            auditLogReason(builder.reason)
        }
    }

    public suspend fun deleteGuildBan(guildId: Snowflake, userId: Snowflake, reason: String? = null): Unit =
        client.delete(Routes.Guilds.ById.Bans) {
            auditLogReason(reason)
        }.body()

    public suspend fun getGuildRoles(guildId: Snowflake): List<DiscordRole> =
        client.get(Routes.Guilds.ById.Roles(guildId)).body()

    public suspend inline fun createGuildRole(
        guildId: Snowflake,
        builder: RoleCreateBuilder.() -> Unit = {},
    ): DiscordRole {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.post(Routes.Guilds.ById.Roles(guildId)) {
            val createBuilder = RoleCreateBuilder().apply(builder)
            setBody(createBuilder.toRequest())
            auditLogReason(createBuilder.reason)
        }.body()
    }

    public suspend inline fun modifyGuildRolePosition(
        guildId: Snowflake,
        builder: RolePositionsModifyBuilder.() -> Unit
    ): List<DiscordRole> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return client.patch(Routes.Guilds.ById.Roles(guildId)) {
            val modifyBuilder = RolePositionsModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend inline fun modifyGuildRole(
        guildId: Snowflake,
        roleId: Snowflake,
        builder: RoleModifyBuilder.() -> Unit
    ): DiscordRole {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return client.patch(Routes.Guilds.ById.Roles.ById(guildId, roleId)) {
            val modifyBuilder = RoleModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend fun modifyGuildMFALevel(
        guildId: Snowflake,
        level: MFALevel,
        reason: String? = null,
    ): GuildMFALevelModifyResponse =
        client.patch(Routes.Guilds.ById.MFA(guildId)) {
            val request = GuildMFALevelModifyRequest(level)
            setBody(request)
            auditLogReason(reason)
    }.body()

    public suspend fun deleteGuildRole(guildId: Snowflake, roleId: Snowflake, reason: String? = null): Unit {
        client.delete(Routes.Guilds.ById.Roles.ById(guildId, roleId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun getGuildPruneCount(guildId: Snowflake, days: Int = 7): GetPruneResponse =
        client.get(Routes.Guilds.ById.Prune(guildId)) {
            parameter("days", days)
        }.body()

    public suspend fun beginGuildPrune(
        guildId: Snowflake,
        days: Int = 7,
        computePruneCount: Boolean = true,
        reason: String? = null,
    ): PruneResponse = client.post(Routes.Guilds.ById.Prune(guildId)) {
        parameter("days", days)
        parameter("compute_prune_count", computePruneCount)
        auditLogReason(reason)
    }.body()

    public suspend fun getGuildVoiceRegions(guildId: Snowflake): List<DiscordVoiceRegion> =
        client.get(Routes.Guilds.ById.VoiceStates(guildId)).body()

    public suspend fun getGuildInvites(guildId: Snowflake): List<DiscordInviteWithMetadata> =
        client.get(Routes.Guilds.ById.Invites(guildId)).body()

    public suspend fun getGuildIntegrations(guildId: Snowflake): List<DiscordIntegration> =
        client.get(Routes.Guilds.ById.Integrations(guildId)).body()

    public suspend fun createGuildIntegration(guildId: Snowflake, request: GuildIntegrationCreateRequest): Unit =
        client.post(Routes.Guilds.ById.Integrations(guildId)) {
            setBody(request)
        }.body()

    public suspend inline fun modifyGuildIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        builder: IntegrationModifyBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        client.patch(Routes.Guilds.ById.Integrations.ById(guildId, integrationId)) {
            val modifyBuilder = IntegrationModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun deleteGuildIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        reason: String? = null,
    ) {
        client.delete(Routes.Guilds.ById.Integrations.ById(guildId, integrationId)) {
            auditLogReason(reason)
        }
    }
    public suspend fun syncGuildIntegration(guildId: Snowflake, integrationId: Snowflake): Unit {
        client.post(Routes.Guilds.ById.Integrations.ById.Sync(guildId, integrationId))
    }

    public suspend fun getGuildWidget(guildId: Snowflake): DiscordGuildWidget =
        client.get(Routes.Guilds.ById.Widget(guildId)).body()

    public suspend fun modifyGuildWidget(
        guildId: Snowflake,
        request: GuildWidgetModifyRequest,
        reason: String? = null,
    ): DiscordGuildWidget =
        client.patch(Routes.Guilds.ById.Widget(guildId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend inline fun modifyGuildWidget(
        guildId: Snowflake,
        builder: GuildWidgetModifyBuilder.() -> Unit
    ): DiscordGuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val modifyBuilder = GuildWidgetModifyBuilder().apply(builder)
        return modifyGuildWidget(guildId, modifyBuilder.toRequest(), modifyBuilder.reason)
    }

    public suspend fun getVanityInvite(guildId: Snowflake): DiscordPartialInvite =
        client.get(Routes.Guilds.ById.VanityUrl(guildId)).body()

    public suspend fun modifyCurrentUserNickname(
        guildId: Snowflake,
        request: CurrentUserNicknameModifyRequest,
        reason: String? = null,
    ): CurrentUserNicknameModifyResponse =
        client.patch(Routes.Guilds.ById.Members.Me.Nick(guildId)) {
            setBody(request)
            auditLogReason(reason)
    }.body()

    public suspend fun getGuildWelcomeScreen(guildId: Snowflake): DiscordWelcomeScreen =
        client.get(Routes.Guilds.ById.WelcomeScreen(guildId)).body()

    public suspend fun modifyGuildWelcomeScreen(
        guildId: Snowflake,
        request: GuildWelcomeScreenModifyRequest,
        reason: String? = null,
    ): DiscordWelcomeScreen = client.patch(Routes.Guilds.ById.WelcomeScreen(guildId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend fun modifyCurrentVoiceState(guildId: Snowflake, request: CurrentVoiceStateModifyRequest): Unit =
        client.patch(Routes.Guilds.ById.VoiceStates.Me(guildId)) {
            setBody(request)
        }.body()

    public suspend fun modifyVoiceState(guildId: Snowflake, userId: Snowflake, request: VoiceStateModifyRequest): Unit =
        client.patch(Routes.Guilds.ById.VoiceStates.ById(guildId, userId)) {
            setBody(request)
        }.body()

    public suspend fun listActiveThreads(guildId: Snowflake): ListThreadsResponse =
        client.get(Routes.Guilds.ById.Threads.Active(guildId)).body()

    public suspend fun listScheduledEvents(
        guildId: Snowflake,
        withUserCount: Boolean? = null,
    ): List<DiscordGuildScheduledEvent> = client.get(Routes.Guilds.ById.ScheduledEvents(guildId)) {
        if (withUserCount != null) {
            parameter("with_user_count", withUserCount)
        }
    }.body()

    public suspend fun getScheduledEvent(guildId: Snowflake, eventId: Snowflake): DiscordGuildScheduledEvent =
        client.get(Routes.Guilds.ById.ScheduledEvents.ById(guildId, eventId)).body()

    public suspend fun createScheduledEvent(
        guildId: Snowflake,
        request: GuildScheduledEventCreateRequest,
        reason: String? = null,
    ): DiscordGuildScheduledEvent =
        client.post(Routes.Guilds.ById.ScheduledEvents(guildId)) {
            setBody(request)
            auditLogReason(reason)
    }.body()

    public suspend fun modifyScheduledEvent(
        guildId: Snowflake,
        eventId: Snowflake,
        request: ScheduledEventModifyRequest,
        reason: String? = null,
    ): DiscordGuildScheduledEvent =
        client.patch(Routes.Guilds.ById.ScheduledEvents.ById(guildId, eventId)) {
            setBody(request)
            auditLogReason(reason)
    }.body()

    public suspend fun deleteScheduledEvent(guildId: Snowflake, eventId: Snowflake): Unit =
        client.delete(Routes.Guilds.ById.ScheduledEvents.ById(guildId, eventId)).body()

    public suspend fun getScheduledEventUsers(
        guildId: Snowflake,
        eventId: Snowflake,
        position: Position.BeforeOrAfter? = null,
        withMember: Boolean? = null,
        limit: Int? = null,
    ): List<GuildScheduledEventUsersResponse> =
        client.get(Routes.Guilds.ById.ScheduledEvents.ById.Users(guildId, eventId)) {
        limit?.let { parameter("limit", it) }
        withMember?.let { parameter("with_member", it) }
        position?.let { parameter(it.key, it.value) }
    }.body()


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

public suspend inline fun GuildService.createForumChannel(
    guildId: Snowflake,
    name: String,
    builder: ForumChannelCreateBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = ForumChannelCreateBuilder(name).apply(builder)
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
    builder: CurrentVoiceStateModifyBuilder.() -> Unit,
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = CurrentVoiceStateModifyBuilder().apply(builder).toRequest()
    modifyCurrentVoiceState(guildId, request)
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
