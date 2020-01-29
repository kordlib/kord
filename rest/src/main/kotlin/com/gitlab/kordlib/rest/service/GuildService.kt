package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.entity.DiscordChannel
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.GuildChannelPositionModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.NewsChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.TextChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildModifyBuilder
import com.gitlab.kordlib.rest.builder.member.MemberAddBuilder
import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.rest.builder.role.RoleModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RolePositionsModifyBuilder
import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route

class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit) = call(Route.GuildPost) {
        body(GuildCreateRequest.serializer(), GuildCreateBuilder().apply(builder).toRequest())
    }

    suspend fun getGuild(guildId: String) = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
    }

    suspend inline fun modifyGuild(guildId: String, builder: GuildModifyBuilder.() -> Unit) = call(Route.GuildPatch) {
        keys[Route.GuildId] = guildId
        val modifyBuilder = GuildModifyBuilder().apply(builder)
        body(GuildModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuild(guildId: String) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: String) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: String, channel: GuildCreateChannelRequest, reason: String? = null) = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(GuildCreateChannelRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend inline fun modifyGuildChannelPosition(guildId: String, builder: GuildChannelPositionModifyBuilder.() -> Unit) = call(Route.GuildChannelsPatch) {
        keys[Route.GuildId] = guildId
        val modifyBuilder = GuildChannelPositionModifyBuilder().apply(builder)
        body(GuildChannelPositionModifyRequest.Serializer, modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildMember(guildId: String, userId: String) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: String, position: Position? = null, limit: Int = 1) = call(Route.GuildMembersGet) {
        keys[Route.GuildId] = guildId
        if (position != null) {
            parameter(position.key, position.value)
        }
        parameter("limit", "$limit")
    }

    suspend fun addGuildMember(guildId: String, userId: String, builder: MemberAddBuilder.() -> Unit) = call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberAddRequest.serializer(), MemberAddBuilder().also(builder).toRequest())
    }

    suspend inline fun modifyGuildMember(guildId: String, userId: String, builder: MemberModifyBuilder.() -> Unit) = call(Route.GuildMemberPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        val modifyBuilder = MemberModifyBuilder().apply(builder)
        body(GuildMemberModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun addRoleToGuildMember(guildId: String, userId: String, roleId: String, reason: String? = null) = call(Route.GuildMemberRolePut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteRoleFromGuildMember(guildId: String, userId: String, roleId: String, reason: String? = null) = call(Route.GuildMemberRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildMember(guildId: String, userId: String, reason: String? = null) = call(Route.GuildMemberDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildBans(guildId: String) = call(Route.GuildBansGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildBan(guildId: String, userId: String) = call(Route.GuildBanGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend inline fun addGuildBan(guildId: String, userId: String, builder: BanCreateBuilder.() -> Unit) = call(Route.GuildBanPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        val createBuilder = BanCreateBuilder().apply(builder)
        body(GuildBanAddRequest.serializer(), createBuilder.toRequest())
        createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildBan(guildId: String, userId: String, reason: String? = null) = call(Route.GuildBanDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildRoles(guildId: String) = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend inline fun createGuildRole(guildId: String, builder: RoleCreateBuilder.() -> Unit = {}) = call(Route.GuildRolePost) {
        keys[Route.GuildId] = guildId
        val createBuilder = RoleCreateBuilder().apply(builder)
        body(GuildRoleCreateRequest.serializer(), createBuilder.toRequest())
        createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend inline fun modifyGuildRolePosition(guildId: String, builder: RolePositionsModifyBuilder.() -> Unit) = call(Route.GuildRolesPatch) {
        keys[Route.GuildId] = guildId
        val modifyBuilder = RolePositionsModifyBuilder().apply(builder)
        body(GuildRolePositionModifyRequest.Serializer, modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend inline fun modifyGuildRole(guildId: String, roleId: String, builder: RoleModifyBuilder.() -> Unit) = call(Route.GuildRolePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        val modifyBuilder = RoleModifyBuilder().apply(builder)
        body(GuildRoleModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildRole(guildId: String, roleId: String, reason: String? = null) = call(Route.GuildRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildPruneCount(guildId: String, days: Int = 7) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
    }

    suspend fun beginGuildPrune(guildId: String, days: Int = 7, computePruneCount: Boolean = true, reason: String? = null) = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
        parameter("days", days)
        parameter("compute_prune_count", computePruneCount)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildVoiceRegions(guildId: String) = call(Route.GuildVoiceRegionsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildInvites(guildId: String) = call(Route.GuildInvitesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildIntegrations(guildId: String) = call(Route.GuildIntegrationGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildIntegration(guildId: String, integration: GuildIntegrationCreateRequest) = call(Route.GuildIntegrationPost) {
        keys[Route.GuildId] = guildId
        body(GuildIntegrationCreateRequest.serializer(), integration)
    }

    suspend fun modifyGuildIntegration(guildId: String, integrationId: String, integration: GuildIntegrationModifyRequest) = call(Route.GuildIntegrationPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
        body(GuildIntegrationModifyRequest.serializer(), integration)
    }

    suspend fun deleteGuildIntegration(guildId: String, integrationId: String) = call(Route.GuildIntegrationDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
    }

    suspend fun syncGuildIntegration(guildId: String, integrationId: String) = call(Route.GuildIntegrationSyncPost) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
    }

    suspend fun getGuildEmbed(guildId: String) = call(Route.GuildEmbedGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuildEmbed(guildId: String, embed: GuildEmbedModifyRequest) = call(Route.GuildEmbedPatch) {
        keys[Route.GuildId] = guildId
        body(GuildEmbedModifyRequest.serializer(), embed)
    }

    suspend fun getVanityInvite(guildId: String) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(guildId: String, nick: CurrentUserNicknameModifyRequest) = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(CurrentUserNicknameModifyRequest.serializer(), nick)
    }

}

suspend inline fun GuildService.createTextChannel(guildId: String, builder: TextChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = TextChannelCreateBuilder().apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

suspend inline fun GuildService.createNewsChannel(guildId: String, builder: NewsChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = NewsChannelCreateBuilder().apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}

suspend inline fun GuildService.createVoiceChannel(guildId: String, builder: VoiceChannelCreateBuilder.() -> Unit): DiscordChannel {
    val createBuilder = VoiceChannelCreateBuilder().apply(builder)
    return createGuildChannel(guildId, createBuilder.toRequest(), createBuilder.reason)
}
