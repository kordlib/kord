package dev.kord.rest.service

import dev.kord.common.entity.DiscordAuditLog
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

public class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend inline fun getAuditLogs(
        guildId: Snowflake,
        builder: AuditLogGetRequestBuilder.() -> Unit,
    ): DiscordAuditLog {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = AuditLogGetRequestBuilder().apply(builder).toRequest()
        return getAuditLogs(guildId, request)
    }

    public suspend fun getAuditLogs(
        guildId: Snowflake,
        request: AuditLogGetRequest,
    ): DiscordAuditLog = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        request.userId?.let { parameter("user_id", it) }
        request.action?.let { parameter("action_type", "${it.value}") }
        request.before?.let { parameter("before", it) }
        request.limit?.let { parameter("limit", it) }
    }
}
