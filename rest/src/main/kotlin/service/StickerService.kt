package dev.kord.rest.service

import dev.kord.common.entity.DiscordMessageSticker
import dev.kord.common.entity.DiscordStickerPack
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.GuildStickerCreateRequest
import dev.kord.rest.json.request.GuildStickerModifyRequest
import dev.kord.rest.json.request.MultipartGuildStickerCreateRequest
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.route.Route

class StickerService(requestHandler: KtorRequestHandler) : RestService(requestHandler) {


    suspend fun getNitroStickerPacks(): List<DiscordStickerPack> = call(Route.NitroStickerPacks)

    suspend fun getGuildStickers(guildId: Snowflake): List<DiscordMessageSticker> = call(Route.GuildStickersGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getSticker(id: Snowflake): DiscordMessageSticker = call(Route.StickerGet) {
        keys[Route.StickerId] = id
    }

    suspend fun getGuildSticker(guildId: Snowflake, id: Snowflake): DiscordMessageSticker =
        call(Route.GuildStickerGet) {
            keys[Route.GuildId] = guildId
            keys[Route.StickerId] = id
        }

    suspend fun createGuildSticker(guildId: Snowflake, multipartRequest: MultipartGuildStickerCreateRequest) =
        call(Route.GuildStickerPost) {
            keys[Route.GuildId] = guildId
            body(GuildStickerCreateRequest.serializer(), multipartRequest.request)
            file(multipartRequest.file)
        }

    suspend fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        request: GuildStickerModifyRequest
    ) = call(Route.GuildStickerPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.StickerId] = id
        body(GuildStickerModifyRequest.serializer(), request)
    }

    suspend fun deleteSticker(guildId: Snowflake, id: Snowflake) = call(Route.GuildStickerDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.StickerId] = id
    }

}