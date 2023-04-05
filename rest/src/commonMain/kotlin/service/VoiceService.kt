package dev.kord.rest.service

import dev.kord.common.entity.DiscordVoiceRegion
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class VoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getVoiceRegions(): List<DiscordVoiceRegion> = call(Route.VoiceRegionsGet)
}
