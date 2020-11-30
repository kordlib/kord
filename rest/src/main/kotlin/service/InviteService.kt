package dev.kord.rest.service

import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class InviteService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getInvite(code: String, withCounts: Boolean) = call(Route.InviteGet) {
        keys[Route.InviteCode] = code
        parameter("with_counts", "$withCounts")
    }

    suspend fun deleteInvite(code: String, reason: String? = null) = call(Route.InviteDelete) {
        keys[Route.InviteCode] = code
        reason?.let { header("X-Audit-Log-Reason", it) }
    }
}