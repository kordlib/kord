package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.json.response.ApplicationInfoResponse
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class ApplicationService(handler: RequestHandler) : RestService(handler) {

    @OptIn(KordUnstableApi::class)
    suspend fun getCurrentApplicationInfo() : ApplicationInfoResponse = call(Route.CurrentApplicationInfo)

}