package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.response.AuditLogEventResponse
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getAuditLogs(
            guildId: String,
            userId: String? = null,
            action: AuditLogEventResponse? = null,
            before: String? = null,
            limit: Int = 50
    ) = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        userId?.let { parameter("user_id", it) }
        action?.let { parameter("action_type", "${it.code}") }
        before?.let { parameter("before", it) }
        parameter("limit", "$limit")
        }
    }
