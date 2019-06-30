package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.json.request.CreateDMRequest
import com.gitlab.hopebaron.rest.json.request.CreateGroupDMRequest
import com.gitlab.hopebaron.rest.json.request.ModifyCurrentUserRequest
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route

class UserService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getCurrentUser() = call(Route.CurrentUserGet)
    suspend fun getUser(userId: String) = call(Route.UserGet) {
        keys[Route.UserId] = userId
    }

    suspend fun getCurrentUserGuilds() = call(Route.CurrentUsersGuildsGet)

    suspend fun leaveGuild(guildId: String) = call(Route.GuildLeave) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getUserConnections() = call(Route.UserConnectionsGet)

    suspend fun createDM(dm: CreateDMRequest) = call(Route.DMPost) {
        body(CreateDMRequest.serializer(), dm)
    }


    suspend fun createGroupDM(dm: CreateGroupDMRequest) = call(Route.DMPost) {
        body(CreateGroupDMRequest.serializer(), dm)
    }

    suspend fun modifyCurrentUser(user: ModifyCurrentUserRequest) = call(Route.CurrentUserPatch) {
        body(ModifyCurrentUserRequest.serializer(), user)
    }
}