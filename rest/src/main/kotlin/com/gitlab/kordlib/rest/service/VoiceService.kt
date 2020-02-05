package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class VoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getVoiceRegions() = call(Route.VoiceRegionsGet)

}
