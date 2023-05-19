package dev.kord.rest.service

import dev.kord.common.entity.DiscordInvite
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*

public class InviteService(val client: HttpClient) {

    public suspend fun getInvite(
        code: String,
        withCounts: Boolean? = null,
        withExpiration: Boolean? = null,
        guildScheduledEventId: Snowflake? = null,
    ): DiscordInvite =
        client.get(Routes.Invites.ById(code)) {
            withCounts?.let { parameter("with_counts", it) }
            withExpiration?.let { parameter("with_expiration", it) }
            guildScheduledEventId?.let { parameter("guild_scheduled_event_id", it) }
        }.body()
    public suspend fun deleteInvite(code: String, reason: String? = null): DiscordInvite =
        client.delete(Routes.Invites.ById(code)) {
            auditLogReason(reason)
        }.body()
}
