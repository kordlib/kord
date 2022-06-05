package dev.kord.rest.service

import dev.kord.common.entity.DiscordApplication
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class ApplicationService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getCurrentApplicationInfo(): DiscordApplication = call(Route.CurrentApplicationInfo)
}
