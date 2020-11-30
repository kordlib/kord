package dev.kord.rest.service

import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class VoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getVoiceRegions() = call(Route.VoiceRegionsGet)

}
