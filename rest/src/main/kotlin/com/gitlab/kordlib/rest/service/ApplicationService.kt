package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.response.ApplicationInfoResponse
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class ApplicationService(handler: RequestHandler) : RestService(handler) {

    suspend fun getCurrentApplicationInfo() : ApplicationInfoResponse = call(Route.CurrentApplicationInfo)

}