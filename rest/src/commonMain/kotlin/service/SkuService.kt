package dev.kord.rest.service

import dev.kord.common.entity.DiscordSku
import dev.kord.common.entity.Snowflake
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class SkuService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getSkus(applicationId: Snowflake): List<DiscordSku> = call(Route.SkusGet) {
        keys[Route.ApplicationId] = applicationId
    }

}