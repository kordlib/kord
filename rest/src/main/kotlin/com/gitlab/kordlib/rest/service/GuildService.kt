package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createGuild(guild: GuildCreatePostRequest) = call(Route.GuildPost) {
        body(GuildCreatePostRequest.serializer(), guild)
    }

    suspend fun getGuild(guildId: String) = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuild(guildId: String, guild: GuildModifyPatchRequest, reason: String? = null) = call(Route.GuildPatch) {
        keys[Route.GuildId] = guildId
        body(GuildModifyPatchRequest.serializer(), guild)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuild(guildId: String) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: String) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: String, channel: GuildChannelCreatePostRequest, reason: String? = null) = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(GuildChannelCreatePostRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun modifyGuildChannelPosition(guildId: String, channel: GuildChannelPositionModifyPatchRequest) = call(Route.GuildChannelsPatch) {
        keys[Route.GuildId] = guildId
        body(GuildChannelPositionModifyPatchRequest.serializer(), channel)
    }

    suspend fun getGuildMember(guildId: String, userId: String) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: String) = call(Route.GuildMembersGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun addGuildMember(guildId: String, userId: String, member: GuildMemberAddPutRequest, reason: String? = null) = call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberAddPutRequest.serializer(), member)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun modifyGuildMember(guildId: String, userId: String, member: GuildMemberModifyPatchRequest, reason: String? = null) = call(Route.GuildMemberPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildMemberModifyPatchRequest.serializer(), member)
        reason?.let { header("X-Audit-Log-Reason", it) }
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

    suspend fun addGuildBan(guildId: String, userId: String, ban: GuildBanAddPutRequest, reason: String? = null) = call(Route.GuildBanPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(GuildBanAddPutRequest.serializer(), ban)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildBan(guildId: String, userId: String, reason: String? = null) = call(Route.GuildBanDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildRoles(guildId: String) = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildRole(guildId: String, role: GuildRoleCreatePostRequest, reason: String? = null) = call(Route.GuildRolePost) {
        keys[Route.GuildId] = guildId
        body(GuildRoleCreatePostRequest.serializer(), role)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun modifyGuildRolePosition(guildId: String, role: GuildRolePositionModifyPatchRequest) = call(Route.GuildRolesPatch) {
        keys[Route.GuildId] = guildId
        body(GuildRolePositionModifyPatchRequest.serializer(), role)
    }


    suspend fun modifyGuildRole(guildId: String, roleId: String, role: GuildRoleModifyPatchRequest, reason: String? = null) = call(Route.GuildRolePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        body(GuildRoleModifyPatchRequest.serializer(), role)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteGuildRole(guildId: String, roleId: String, reason: String? = null) = call(Route.GuildRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getGuildPruneCount(guildId: String) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun beginGuildPrune(guildId: String, reason: String? = null) = call(Route.GuildPrunePost) {
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

    suspend fun createGuildIntegration(guildId: String, integration: GuildIntegrationCreatePostRequest) = call(Route.GuildIntegrationPost) {
        keys[Route.GuildId] = guildId
        body(GuildIntegrationCreatePostRequest.serializer(), integration)
    }

    suspend fun modifyGuildIntegration(guildId: String, integrationId: String, integration: GuildIntegrationModifyPatchRequest) = call(Route.GuildIntegrationPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
        body(GuildIntegrationModifyPatchRequest.serializer(), integration)
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

    suspend fun modifyGuildEmbed(guildId: String, embed: GuildEmbedModifyPatchRequest) = call(Route.GuildEmbedPatch) {
        keys[Route.GuildId] = guildId
        body(GuildEmbedModifyPatchRequest.serializer(), embed)
    }

    suspend fun getVanityInvite(guildId: String) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(guildId: String, nick: CurrentUserNicknameModifyPatchRequest) = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(CurrentUserNicknameModifyPatchRequest.serializer(), nick)

    }


}