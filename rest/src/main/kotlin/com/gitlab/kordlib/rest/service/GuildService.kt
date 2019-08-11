package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route
import io.ktor.http.Parameters

class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createGuild(guild: GuildCreateRequest) = call(Route.GuildPost) {
        body(GuildCreateRequest.serializer(), guild)
    }

    suspend fun getGuild(guildId: String) = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuild(guildId: String, guild: GuildModifyRequest) = call(Route.GuildPatch) {
        keys[Route.GuildId] = guildId
        body(GuildModifyRequest.serializer(), guild)
    }

    suspend fun deleteGuild(guildId: String) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: String) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: String, channel: GuildCreateChannelRequest) = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(GuildCreateChannelRequest.serializer(), channel)
    }

    suspend fun modifyGuildChannelPosition(guildId: String, channel: GuildChannelPositionModifyRequest) = call(Route.GuildChannelsPatch) {
        keys[Route.GuildId] = guildId
        body(GuildChannelPositionModifyRequest.Serializer, channel)
    }

    suspend fun getGuildMember(guildId: String, userId: String) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: String, position: Position? = null, limit: Int = 1) = call(Route.GuildMembersGet) {
        keys[Route.GuildId] = guildId
        parameters = Parameters.build {
            if (position != null) {
                append(position.key, position.value)
            }
            append("limit", "$limit")

        }
    }

    suspend fun addGuildMember(guildId: String, userId: String, member: GuildMemberAddRequest) = call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberAddRequest.serializer(), member)
    }

    suspend fun modifyGuildMember(guildId: String, userId: String, member: GuildMemberModifyRequest) = call(Route.GuildMemberPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberModifyRequest.serializer(), member)
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

    suspend fun addGuildBan(guildId: String, userId: String, ban: GuildBanAddRequest) = call(Route.GuildBanPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildBanAddRequest.serializer(), ban)
    }

    suspend fun deleteGuildBan(guildId: String, userId: String, reason: String? = null) = call(Route.GuildBanDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildRoles(guildId: String) = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildRole(guildId: String, role: GuildRoleCreateRequest) = call(Route.GuildRolePost) {
        keys[Route.GuildId] = guildId
        body(GuildRoleCreateRequest.serializer(), role)
    }

    suspend fun modifyGuildRolePosition(guildId: String, role: GuildRolePositionModifyRequest) = call(Route.GuildRolesPatch) {
        keys[Route.GuildId] = guildId
        body(GuildRolePositionModifyRequest.Serializer, role)
    }


    suspend fun modifyGuildRole(guildId: String, roleId: String, role: GuildRoleModifyRequest) = call(Route.GuildRolePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        body(GuildRoleModifyRequest.serializer(), role)
    }

    suspend fun deleteGuildRole(guildId: String, roleId: String, reason: String? = null) = call(Route.GuildRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildPruneCount(guildId: String, request: GuildPruneGetRequest) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun beginGuildPrune(guildId: String, request: GuildPruneBeginRequest) = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
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