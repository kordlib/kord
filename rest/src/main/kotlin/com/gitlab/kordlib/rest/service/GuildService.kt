package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.*
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildModifyBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildWidgetModifyBuilder
import com.gitlab.kordlib.rest.builder.integration.IntegrationModifyBuilder
import com.gitlab.kordlib.rest.builder.member.MemberAddBuilder
import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.rest.builder.role.RoleModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RolePositionsModifyBuilder
import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route
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

    suspend fun addGuildMember(guildId: Snowflake, userId: Snowflake, token: String, builder: MemberAddBuilder.() -> Unit) = call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberAddRequest.serializer(), MemberAddBuilder(token).also(builder).toRequest())
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildMember(guildId: Snowflake, userId: Snowflake, builder: MemberModifyBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        call(Route.GuildMemberPatch) {
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

    suspend inline fun modifyGuildRolePosition(guildId: Snowflake, builder: RolePositionsModifyBuilder.() -> Unit) = call(Route.GuildRolesPatch) {
        keys[Route.GuildId] = guildId
        val modifyBuilder = RolePositionsModifyBuilder().apply(builder)
        body(GuildRolePositionModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend inline fun modifyGuildRole(guildId: Snowflake, roleId: Snowflake, builder: RoleModifyBuilder.() -> Unit) = call(Route.GuildRolePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        val modifyBuilder = RoleModifyBuilder().apply(builder)
        body(GuildRoleModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
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

    suspend inline fun modifyGuildIntegration(guildId: Snowflake, integrationId: Snowflake, builder: IntegrationModifyBuilder.() -> Unit) = call(Route.GuildIntegrationPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
        body(GuildIntegrationModifyRequest.serializer(), IntegrationModifyBuilder().apply(builder).toRequest())
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

    suspend inline fun modifyGuildWidget(guildId: Snowflake, builder: GuildWidgetModifyBuilder.() -> Unit): DiscordGuildWidget =
            modifyGuildWidget(guildId, GuildWidgetModifyBuilder().apply(builder).toRequest())

    suspend fun getVanityInvite(guildId: Snowflake) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(guildId: Snowflake, nick: CurrentUserNicknameModifyRequest) = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(CurrentUserNicknameModifyRequest.serializer(), nick)
    }

}

suspend inline fun GuildService.createTextChannel(guildId: Snowflake, name: String, builder: TextChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = TextChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

suspend inline fun GuildService.createNewsChannel(guildId: Snowflake, name: String, builder: NewsChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = NewsChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

suspend inline fun GuildService.createVoiceChannel(guildId: Snowflake, name: String, builder: VoiceChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = VoiceChannelCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

suspend inline fun GuildService.createCategory(guildId: Snowflake, name: String, builder: CategoryCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = CategoryCreateBuilder(name).apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}
