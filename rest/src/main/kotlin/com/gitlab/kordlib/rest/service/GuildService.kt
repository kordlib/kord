package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class GuildService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createGuild(guild: com.gitlab.kordlib.rest.json.request.CreateGuildRequest) = call(Route.GuildPost) {
        body(com.gitlab.kordlib.rest.json.request.CreateGuildRequest.serializer(), guild)
    }

    suspend fun getGuild(guildId: String) = call(Route.GuildGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyGuild(guildId: String, guild: com.gitlab.kordlib.rest.json.request.ModifyGuildRequest) = call(Route.GuildPatch) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildRequest.serializer(), guild)
    }

    suspend fun deleteGuild(guildId: String) = call(Route.GuildDelete) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildChannels(guildId: String) = call(Route.GuildChannelsGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildChannel(guildId: String, channel: com.gitlab.kordlib.rest.json.request.CreateGuildChannelRequest) = call(Route.GuildChannelsPost) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.CreateGuildChannelRequest.serializer(), channel)
    }

    suspend fun modifyGuildChannelPosition(guildId: String, channel: com.gitlab.kordlib.rest.json.request.ModifyGuildChannelPositionRequest) = call(Route.GuildChannelsPatch) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildChannelPositionRequest.serializer(), channel)
    }

    suspend fun getGuildMember(guildId: String, userId: String) = call(Route.GuildMemberGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildMembers(guildId: String) = call(Route.GuildMembersGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun addGuildMember(guildId: String, userId: String, member: com.gitlab.kordlib.rest.json.request.AddGuildMemberRequest) = call(Route.GuildMemberPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(com.gitlab.kordlib.rest.json.request.AddGuildMemberRequest.serializer(), member)
    }

    suspend fun modifyGuildMember(guildId: String, userId: String, member: com.gitlab.kordlib.rest.json.request.ModifyGuildMemberRequest) = call(Route.GuildMemberPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildMemberRequest.serializer(), member)
    }

    suspend fun addRoleToGuildMember(guildId: String, userId: String, roleId: String) = call(Route.GuildMemberRolePut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
    }

    suspend fun deleteRoleFromGuildMember(guildId: String, userId: String, roleId: String) = call(Route.GuildMemberRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        keys[Route.RoleId] = roleId
    }

    suspend fun deleteGuildMember(guildId: String, userId: String) = call(Route.GuildMemberDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildBans(guildId: String) = call(Route.GuildBansGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getGuildBan(guildId: String, userId: String) = call(Route.GuildBanGet) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun addGuildBan(guildId: String, userId: String, ban: com.gitlab.kordlib.rest.json.request.AddGuildBanRequest) = call(Route.GuildBanPut) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
        body(com.gitlab.kordlib.rest.json.request.AddGuildBanRequest.serializer(), ban)
    }

    suspend fun deleteGuildBan(guildId: String, userId: String) = call(Route.GuildBanDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.UserId] = userId
    }

    suspend fun getGuildRoles(guildId: String) = call(Route.GuildRolesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun createGuildRole(guildId: String, role: com.gitlab.kordlib.rest.json.request.CreateGuildRoleRequest) = call(Route.GuildRolePost) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.CreateGuildRoleRequest.serializer(), role)
    }

    suspend fun modifyGuildRolePosition(guildId: String, role: com.gitlab.kordlib.rest.json.request.ModifyGuildRolePositionRequest) = call(Route.GuildRolesPatch) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildRolePositionRequest.serializer(), role)
    }


    suspend fun modifyGuildRole(guildId: String, roleId: String, role: com.gitlab.kordlib.rest.json.request.ModifyGuildRoleRequest) = call(Route.GuildRolePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildRoleRequest.serializer(), role)
    }

    suspend fun deleteGuildRole(guildId: String, roleId: String) = call(Route.GuildRoleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.RoleId] = roleId
    }

    suspend fun getGuildPruneCount(guildId: String) = call(Route.GuildPruneCountGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun beginGuildPrune(guildId: String) = call(Route.GuildPrunePost) {
        keys[Route.GuildId] = guildId
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

    suspend fun createGuildIntegration(guildId: String, integration: com.gitlab.kordlib.rest.json.request.CreateGuildIntegrationRequest) = call(Route.GuildIntegrationPost) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.CreateGuildIntegrationRequest.serializer(), integration)
    }

    suspend fun modifyGuildIntegration(guildId: String, integrationId: String, integration: com.gitlab.kordlib.rest.json.request.ModifyGuildIntegrationRequest) = call(Route.GuildIntegrationPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.IntegrationId] = integrationId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildIntegrationRequest.serializer(), integration)
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

    suspend fun modifyGuildEmbed(guildId: String, embed: com.gitlab.kordlib.rest.json.request.ModifyGuildEmbedRequest) = call(Route.GuildEmbedPatch) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.ModifyGuildEmbedRequest.serializer(), embed)
    }

    suspend fun getVanityInvite(guildId: String) = call(Route.GuildVanityInviteGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun modifyCurrentUserNickname(guildId: String, nick: com.gitlab.kordlib.rest.json.request.ModifyCurrentUserNicknameRequest) = call(Route.GuildCurrentUserNickPatch) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.ModifyCurrentUserNicknameRequest.serializer(), nick)

    }


}