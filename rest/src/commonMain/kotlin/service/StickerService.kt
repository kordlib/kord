package dev.kord.rest.service

import dev.kord.common.entity.DiscordMessageSticker
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.Stickers
import dev.kord.rest.builder.guild.StickerModifyBuilder
import dev.kord.rest.json.request.GuildStickerModifyRequest
import dev.kord.rest.json.request.MultipartGuildStickerCreateRequest
import dev.kord.rest.json.response.NitroStickerPacksResponse
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

//TODO("Files")
public class StickerService(public val client: HttpClient) {

    public suspend fun getNitroStickerPacks(): NitroStickerPacksResponse =
        client.get(Routes.NitroStickerPacks).body()

    public suspend fun getGuildStickers(guildId: Snowflake): List<DiscordMessageSticker> =
        client.get(Routes.Guilds.ById.Stickers(guildId)).body()

    public suspend fun getSticker(id: Snowflake): DiscordMessageSticker =
        client.get(Routes.Stickers.ById(id)).body()

    public suspend fun getGuildSticker(guildId: Snowflake, id: Snowflake): DiscordMessageSticker =
        client.get(Routes.Guilds.ById.Stickers(guildId, id)).body()

    public suspend fun createGuildSticker(
        guildId: Snowflake,
        multipartRequest: MultipartGuildStickerCreateRequest,
    ): DiscordMessageSticker = client.post(Routes.Guilds.ById.Stickers(guildId)) {
        setBody(request)
        file(multipartRequest.file)
    }.body()

    public suspend fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        request: GuildStickerModifyRequest,
    ): DiscordMessageSticker = client.patch(Routes.Guilds.ById.Stickers.ById(guildId, id)) {
        setBody(request)
    }.body()

    public suspend inline fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        builder: StickerModifyBuilder.() -> Unit,
    ): DiscordMessageSticker {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = StickerModifyBuilder().apply(builder).toRequest()
        return modifyGuildSticker(guildId, id, request)
    }

    public suspend fun deleteSticker(guildId: Snowflake, id: Snowflake): Unit =
        client.delete(Routes.Guilds.ById.Stickers.ById(guildId, id)).body()
}
