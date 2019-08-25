package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.CurrentUserModifyRequest
import com.gitlab.kordlib.rest.json.request.DMCreateRequest
import com.gitlab.kordlib.rest.json.request.GroupDMCreateRequest
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route

class UserService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getCurrentUser() = call(Route.CurrentUserGet)
    suspend fun getUser(userId: String) = call(Route.UserGet) {
        keys[Route.UserId] = userId
    }

    @Deprecated("this method is deprecated, use the parameterized version instead", replaceWith = ReplaceWith("getCurrentUserGuilds()"), level = DeprecationLevel.HIDDEN)
    suspend fun getCurrentUserGuilds() = call(Route.CurrentUsersGuildsGet)

    suspend fun getCurrentUserGuilds(position: Position? = null, limit: Int = 100) = call(Route.CurrentUsersGuildsGet) {
        if (position != null) {
            parameter(position.key, position.value)
        }

        parameter("limit", "$limit")
    }

    suspend fun leaveGuild(guildId: String) = call(Route.GuildLeave) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getUserConnections() = call(Route.UserConnectionsGet)

    suspend fun createDM(dm: DMCreateRequest) = call(Route.DMPost) {
        body(DMCreateRequest.serializer(), dm)
    }


    suspend fun createGroupDM(dm: GroupDMCreateRequest) = call(Route.DMPost) {
        body(GroupDMCreateRequest.serializer(), dm)
    }

    suspend fun modifyCurrentUser(user: CurrentUserModifyRequest) = call(Route.CurrentUserPatch) {
        body(CurrentUserModifyRequest.serializer(), user)
    }
}