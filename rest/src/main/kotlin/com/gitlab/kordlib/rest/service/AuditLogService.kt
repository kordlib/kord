package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.response.AuditLogEventResponse
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import io.ktor.http.Parameters

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getAuditLogs(
            guildId: String,
            userId: String? = null,
            action: AuditLogEventResponse? = null,
            before: String? = null,
            limit: Int = 50
    ) = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        parameters = Parameters.build {
            userId?.let { append("user_id", it) }
            action?.let { append("action_type", "${it.code}") }
            before?.let { append("before", it) }
            append("limit", "$limit")
        }
    }

}
