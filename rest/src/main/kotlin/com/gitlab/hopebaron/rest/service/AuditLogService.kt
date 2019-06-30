package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.json.response.AuditLogEventResponse
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.Parameters

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getAuditLogs(guildId: String, userId: String, action: AuditLogEventResponse, before: String, limit: Int = 50) = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        parameters = Parameters.build {
            append("user_id", userId)
            append("action_type", "${action.code}")
            append("before", before)
            append("limit", "$limit")
        }
    }
}
