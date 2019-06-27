package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.ParametersBuilder

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {
    // TODO add action type
    suspend fun getAuditLogs(guildId: String, userId: String, action: Int, before: String, limit: Int = 50) = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        parameters = with(ParametersBuilder()) {
            append("user_id", userId)
            append("action_type", "$action")
            append("before", before)
            append("limit", "$limit")
            build()
        }
    }
}