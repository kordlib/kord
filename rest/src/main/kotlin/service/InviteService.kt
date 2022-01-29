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
        if (withCounts != null) {
            parameter("with_counts", withCounts)
        }
        if (withExpiration != null) {
            parameter("with_expiration", withExpiration)
        }
        if (guildScheduledEventId != null) {
            parameter("guild_scheduled_event_id", guildScheduledEventId)
        }
    }

    public suspend fun deleteInvite(code: String, reason: String? = null): DiscordInvite = call(Route.InviteDelete) {
        keys[Route.InviteCode] = code
        auditLogReason(reason)
    }
}
