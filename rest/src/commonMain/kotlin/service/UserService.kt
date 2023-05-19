package dev.kord.rest.service

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordPartialGuild
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.Channels
import dev.kord.rest.Connections
import dev.kord.rest.Me
import dev.kord.rest.builder.user.CurrentUserModifyBuilder
import dev.kord.rest.builder.user.GroupDMCreateBuilder
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.response.Connection
import dev.kord.rest.route.Position
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

//TODO("Direct Request overloads")
public class UserService(public val client: HttpClient) {

    public suspend fun getCurrentUser(): DiscordUser = client.put(Routes.Users.Me()).body()

    public suspend fun getUser(userId: Snowflake): DiscordUser = client.put(Routes.Users.ById(userId)).body()

    public suspend fun getCurrentUserGuilds(
        position: Position.BeforeOrAfter? = null,
        limit: Int? = null,
    ): List<DiscordPartialGuild> =
        client.put(Routes.Users.Me()) {
        position?.let { parameter(it.key, it.value) }
        limit?.let { parameter("limit", it) }
    }.body()

    public suspend fun leaveGuild(guildId: Snowflake) {
        client.delete(Routes.Guilds.ById(guildId))
    }

    public suspend fun getUserConnections(): List<Connection> =
        client.put(Routes.Users.Me.Connections()).body()

    public suspend fun createDM(request: DMCreateRequest): DiscordChannel =
        client.post(Routes.Users.Me.Channels()) {
        setBody(request)
    }.body()

    public suspend inline fun createGroupDM(builder: GroupDMCreateBuilder.() -> Unit): DiscordChannel {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.post(Routes.Users.Me.Channels) {
            setBody(GroupDMCreateBuilder().apply(builder).toRequest())
        }.body()
    }

    public suspend inline fun modifyCurrentUser(builder: CurrentUserModifyBuilder.() -> Unit): DiscordUser {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.patch(Routes.Users.Me()) {
            setBody(CurrentUserModifyBuilder().apply(builder).toRequest())
        }.body()
    }
}
