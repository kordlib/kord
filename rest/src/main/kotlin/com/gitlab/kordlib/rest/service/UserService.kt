package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.CurrentUserModifyPatchRequest
import com.gitlab.kordlib.rest.json.request.DMCreatePostRequest
import com.gitlab.kordlib.rest.json.request.GroupDMCreatePostRequest
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

    suspend fun createDM(dm: DMCreatePostRequest) = call(Route.DMPost) {
        body(DMCreatePostRequest.serializer(), dm)
    }


    suspend fun createGroupDM(dm: GroupDMCreatePostRequest) = call(Route.DMPost) {
        body(GroupDMCreatePostRequest.serializer(), dm)
    }

    suspend fun modifyCurrentUser(user: CurrentUserModifyPatchRequest) = call(Route.CurrentUserPatch) {
        body(CurrentUserModifyPatchRequest.serializer(), user)
    }
}