package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.ParametersBuilder

class InviteService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getInvite(code: String, withCounts: Boolean) = call(Route.InviteGet) {
        keys[Route.InviteCode] = code
        parameters = with(ParametersBuilder()) {
            append("with_counts", "$withCounts")
            build()
        }
    }

    suspend fun deleteInvite(code: String) = call(Route.InviteGet) {
        keys[Route.InviteCode] = code
    }
}