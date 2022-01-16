package dev.kord.rest.service

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.user.CurrentUserModifyBuilder
import dev.kord.rest.builder.user.GroupDMCreateBuilder
import dev.kord.rest.json.request.CurrentUserModifyRequest
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.request.GroupDMCreateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class UserService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getCurrentUser() = call(Route.CurrentUserGet)

    suspend fun getUser(userId: Snowflake) = call(Route.UserGet) {
        keys[Route.UserId] = userId
    }

    suspend fun getCurrentUserGuilds(position: Position.BeforeOrAfter? = null, limit: Int? = null) =
        call(Route.CurrentUsersGuildsGet) {
            position?.let { parameter(it.key, it.value) }
            limit?.let { parameter("limit", it) }
        }

    suspend fun leaveGuild(guildId: Snowflake) = call(Route.GuildLeave) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getUserConnections() = call(Route.UserConnectionsGet)

    suspend fun createDM(dm: DMCreateRequest) = call(Route.DMPost) {
        body(DMCreateRequest.serializer(), dm)
    }

    suspend inline fun createGroupDM(builder: GroupDMCreateBuilder.() -> Unit): DiscordChannel {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.DMPost) {
            body(GroupDMCreateRequest.serializer(), GroupDMCreateBuilder().apply(builder).toRequest())
        }
    }

    suspend inline fun modifyCurrentUser(builder: CurrentUserModifyBuilder.() -> Unit): DiscordUser {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.CurrentUserPatch) {
            body(CurrentUserModifyRequest.serializer(), CurrentUserModifyBuilder().apply(builder).toRequest())
        }
    }

}
