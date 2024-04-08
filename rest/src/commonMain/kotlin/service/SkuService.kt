package dev.kord.rest.service

import dev.kord.common.entity.DiscordSKU
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class SkuService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getSkus(): List<DiscordSKU> = call(Route.SkusGet)

}