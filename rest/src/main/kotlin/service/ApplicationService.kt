package dev.kord.rest.service

import dev.kord.rest.json.response.ApplicationInfoResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class ApplicationService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getCurrentApplicationInfo(): ApplicationInfoResponse = call(Route.CurrentApplicationInfo)

}
