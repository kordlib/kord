package dev.kord.rest.service

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordPartialGuild
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.user.CurrentUserModifyBuilder
import dev.kord.rest.builder.user.GroupDMCreateBuilder
import dev.kord.rest.json.request.CurrentUserModifyRequest
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.request.GroupDMCreateRequest
import dev.kord.rest.json.response.Connection
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class UserService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getCurrentUser(): DiscordUser = call(Route.CurrentUserGet)

    public suspend fun getUser(userId: Snowflake): DiscordUser = call(Route.UserGet) {
        keys[Route.UserId] = userId
    }

    public suspend fun getCurrentUserGuilds(
        position: Position.BeforeOrAfter? = null,
        limit: Int? = null,
    ): List<DiscordPartialGuild> = call(Route.CurrentUsersGuildsGet) {
        position?.let { parameter(it.key, it.value) }
        limit?.let { parameter("limit", it) }
    }

    public suspend fun leaveGuild(guildId: Snowflake): Unit = call(Route.GuildLeave) {
        keys[Route.GuildId] = guildId
    }

    public suspend fun getUserConnections(): List<Connection> = call(Route.UserConnectionsGet)

    public suspend fun createDM(dm: DMCreateRequest): DiscordChannel = call(Route.DMPost) {
        body(DMCreateRequest.serializer(), dm)
    }

    public suspend inline fun createGroupDM(builder: GroupDMCreateBuilder.() -> Unit): DiscordChannel {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.DMPost) {
            body(GroupDMCreateRequest.serializer(), GroupDMCreateBuilder().apply(builder).toRequest())
        }
    }

    public suspend inline fun modifyCurrentUser(builder: CurrentUserModifyBuilder.() -> Unit): DiscordUser {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.CurrentUserPatch) {
            body(CurrentUserModifyRequest.serializer(), CurrentUserModifyBuilder().apply(builder).toRequest())
        }
    }
}
