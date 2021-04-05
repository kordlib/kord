package dev.kord.rest.service

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.guild.GuildCreateBuilder
import dev.kord.rest.builder.guild.GuildModifyBuilder
import dev.kord.rest.builder.guild.GuildWidgetModifyBuilder
import dev.kord.rest.builder.guild.WelcomeScreenModifyBuilder
import dev.kord.rest.builder.integration.IntegrationModifyBuilder
import dev.kord.rest.builder.member.MemberAddBuilder
import dev.kord.rest.builder.member.MemberModifyBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.builder.role.RoleModifyBuilder
import dev.kord.rest.builder.role.RolePositionsModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
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
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun deleteGuild(guildId: Snowflake) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: Snowflake) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: Snowflake, channel: GuildChannelCreateRequest, reason: String? = null) = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(GuildChannelCreateRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildChannelPosition(guildId: Snowflake, builder: GuildChannelPositionModifyBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        call(Route.GuildChannelsPatch) {
            keys[Route.GuildId] = guildId
            val modifyBuilder = GuildChannelPositionModifyBuilder().apply(builder)
            body(GuildChannelPositionModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun getGuildMember(guildId: Snowflake, userId: Snowflake) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: Snowflake, position: Position? = null, limit: Int = 1) = call(Route.GuildMembersGet) {
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
    suspend fun addGuildMember(guildId: Snowflake, userId: Snowflake, token: String, builder: MemberAddBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberAddRequest.serializer(), MemberAddBuilder(token).also(builder).toRequest())
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildMember(guildId: Snowflake, userId: Snowflake, builder: MemberModifyBuilder.() -> Unit): DiscordGuildMember {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.GuildMemberPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.UserId] = userId
            val modifyBuilder = MemberModifyBuilder().apply(builder)
            body(GuildMemberModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun addRoleToGuildMember(guildId: Snowflake, userId: Snowflake, roleId: Snowflake, reason: String? = null) = call(Route.GuildMemberRolePut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteRoleFromGuildMember(guildId: Snowflake, userId: Snowflake, roleId: Snowflake, reason: String? = null) = call(Route.GuildMemberRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildMember(guildId: Snowflake, userId: Snowflake, reason: String? = null) = call(Route.GuildMemberDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
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
            createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun deleteGuildBan(guildId: Snowflake, userId: Snowflake, reason: String? = null) = call(Route.GuildBanDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
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
            createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildRolePosition(guildId: Snowflake, builder: RolePositionsModifyBuilder.() -> Unit): List<DiscordRole> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return call(Route.GuildRolesPatch) {
            keys[Route.GuildId] = guildId
            val modifyBuilder = RolePositionsModifyBuilder().apply(builder)
            body(GuildRolePositionModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildRole(guildId: Snowflake, roleId: Snowflake, builder: RoleModifyBuilder.() -> Unit): DiscordRole {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return call(Route.GuildRolePatch) {
            keys[Route.GuildId] = guildId
            keys[Route.RoleId] = roleId
            val modifyBuilder = RoleModifyBuilder().apply(builder)
            body(GuildRoleModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun deleteGuildRole(guildId: Snowflake, roleId: Snowflake, reason: String? = null) = call(Route.GuildRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildPruneCount(guildId: Snowflake, days: Int = 7) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
    }

    suspend fun beginGuildPrune(guildId: Snowflake, days: Int = 7, computePruneCount: Boolean = true, reason: String? = null) = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
        parameter("compute_prune_count", computePruneCount)
        reason?.let { header("X-Audit-Log-Reason", it) }
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

    suspend fun createGuildIntegration(guildId: Snowflake, integration: GuildIntegrationCreateRequest) = call(Route.GuildIntegrationPost) {
        keys[Route.GuildId] = guildId
        body(GuildIntegrationCreateRequest.serializer(), integration)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildIntegration(guildId: Snowflake, integrationId: Snowflake, builder: IntegrationModifyBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
            call(Route.GuildIntegrationPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.IntegrationId] = integrationId
            body(GuildIntegrationModifyRequest.serializer(), IntegrationModifyBuilder().apply(builder).toRequest())
        }
    }

    suspend fun deleteGuildIntegration(guildId: Snowflake, integrationId: Snowflake) = call(Route.GuildIntegrationDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
    }

    suspend fun syncGuildIntegration(guildId: Snowflake, integrationId: Snowflake) = call(Route.GuildIntegrationSyncPost) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
    }

    @Suppress("RedundantSuspendModifier")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("getGuildWidget(guildId)"), DeprecationLevel.ERROR)
    suspend fun getGuildEmbed(guildId: Snowflake): Nothing = throw Exception("Guild embeds were renamed to widgets.")

    @Suppress("RedundantSuspendModifier")
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("modifyGuildWidget(guildId, embed)"), DeprecationLevel.ERROR)
    suspend fun modifyGuildEmbed(guildId: Snowflake, embed: Any): Nothing = throw Exception("Guild embeds were renamed to widgets.")

    suspend fun getGuildWidget(guildId: Snowflake): DiscordGuildWidget = call(Route.GuildWidgetGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuildWidget(guildId: Snowflake, widget: GuildWidgetModifyRequest): DiscordGuildWidget = call(Route.GuildWidgetPatch){
        keys[Route.GuildId] = guildId
        body(GuildWidgetModifyRequest.serializer(), widget)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildWidget(guildId: Snowflake, builder: GuildWidgetModifyBuilder.() -> Unit): DiscordGuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyGuildWidget(guildId, GuildWidgetModifyBuilder().apply(builder).toRequest())
    }

    suspend fun getVanityInvite(guildId: Snowflake) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(guildId: Snowflake, nick: CurrentUserNicknameModifyRequest) = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(CurrentUserNicknameModifyRequest.serializer(), nick)
    }

    suspend fun getGuildWelcomeScreen(guildId: Snowflake) = call(Route.GuildWelcomeScreenGet){
        keys[Route.GuildId] = guildId
    }


    suspend fun modifyGuildWelcomeScreen(guildId: Snowflake, request: GuildWelcomeScreenModifyRequest) = call(Route.GuildWelcomeScreenPatch){
        keys[Route.GuildId] = guildId
        body(GuildWelcomeScreenModifyRequest.serializer(), request)
    }

}
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.modifyGuildWelcomeScreen(guildId: Snowflake, builder: WelcomeScreenModifyBuilder.() -> Unit): DiscordWelcomeScreen {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = WelcomeScreenModifyBuilder().apply(builder)
    return modifyGuildWelcomeScreen(guildId, builder.toRequest())
}
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createTextChannel(guildId: Snowflake, name: String, builder: TextChannelCreateBuilder.() -> Unit): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = TextChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createNewsChannel(guildId: Snowflake, name: String, builder: NewsChannelCreateBuilder.() -> Unit): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = NewsChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createVoiceChannel(guildId: Snowflake, name: String, builder: VoiceChannelCreateBuilder.() -> Unit): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = VoiceChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildService.createCategory(guildId: Snowflake, name: String, builder: CategoryCreateBuilder.() -> Unit): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val createBuilder = CategoryCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}
