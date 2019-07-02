package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route

class VoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getVoiceRegions() = call(Route.VoiceRegionsGet)

}
