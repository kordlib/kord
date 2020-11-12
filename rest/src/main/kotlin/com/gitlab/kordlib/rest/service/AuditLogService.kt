package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.entity.AuditLogEvent
import com.gitlab.kordlib.common.entity.DiscordAuditLog
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.auditlog.AuditLogGetRequestBuilder
import com.gitlab.kordlib.rest.json.request.AuditLogGetRequest
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend inline fun getAuditLogs(
            guildId: Snowflake,
            builder: AuditLogGetRequestBuilder.() -> Unit,
    ): DiscordAuditLog {
        val request = AuditLogGetRequestBuilder().apply(builder).toRequest()
        return getAuditLogs(guildId, request)
    }

    suspend fun getAuditLogs(
            guildId: Snowflake,
            request: AuditLogGetRequest,
    ): DiscordAuditLog = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        request.userId?.let { parameter("user_id", it) }
        request.action?.let { parameter("action_type", "${it.value}") }
        request.before?.let { parameter("before", it) }
        parameter("limit", "${request.limit}")
    }
}
