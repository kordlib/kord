package dev.kord.rest.service

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.response.StickerPackList
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class StickerService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getSticker(id: Snowflake) = call(Route.StickersGet) {
        keys[Route.StickerId] = id
    }

    suspend fun getStickerPacks(): StickerPackList = call(Route.StickerPacksGet)
}
