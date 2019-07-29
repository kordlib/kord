package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.CreateDMRequest
import com.gitlab.kordlib.rest.json.request.CreateGroupDMRequest
import com.gitlab.kordlib.rest.json.request.ModifyCurrentUserRequest
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

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

    suspend fun createDM(dm: com.gitlab.kordlib.rest.json.request.CreateDMRequest) = call(Route.DMPost) {
        body(com.gitlab.kordlib.rest.json.request.CreateDMRequest.serializer(), dm)
    }


    suspend fun createGroupDM(dm: com.gitlab.kordlib.rest.json.request.CreateGroupDMRequest) = call(Route.DMPost) {
        body(com.gitlab.kordlib.rest.json.request.CreateGroupDMRequest.serializer(), dm)
    }

    suspend fun modifyCurrentUser(user: com.gitlab.kordlib.rest.json.request.ModifyCurrentUserRequest) = call(Route.CurrentUserPatch) {
        body(com.gitlab.kordlib.rest.json.request.ModifyCurrentUserRequest.serializer(), user)
    }
}