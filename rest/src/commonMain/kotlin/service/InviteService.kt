package dev.kord.rest.service

import dev.kord.common.entity.DiscordInvite
import dev.kord.common.entity.Snowflake
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route

public class InviteService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getInvite(
        code: String,
        withCounts: Boolean? = null,
        withExpiration: Boolean? = null,
        guildScheduledEventId: Snowflake? = null,
    ): DiscordInvite = call(Route.InviteGet) {
        keys[Route.InviteCode] = code
        withCounts?.let { parameter("with_counts", it) }
        withExpiration?.let { parameter("with_expiration", it) }
        guildScheduledEventId?.let { parameter("guild_scheduled_event_id", it) }
    }

    public suspend fun deleteInvite(code: String, reason: String? = null): DiscordInvite = call(Route.InviteDelete) {
        keys[Route.InviteCode] = code
        auditLogReason(reason)
    }
}
