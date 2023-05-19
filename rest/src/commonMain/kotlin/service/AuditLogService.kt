package dev.kord.rest.service

import dev.kord.common.entity.DiscordAuditLog
import dev.kord.common.entity.Snowflake
import dev.kord.rest.AuditLog
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

public class AuditLogService(public val client: HttpClient) {

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
    ): DiscordAuditLog =
        client.get(Routes.Guilds.ById.AuditLog(guildId)) {
            request.userId?.let { parameter("user_id", it) }
            request.action?.let { parameter("action_type", "${it.value}") }
            request.before?.let { parameter("before", it) }
            request.after?.let { parameter("after", it) }
            request.limit?.let { parameter("limit", it) }
        }.body()
}
