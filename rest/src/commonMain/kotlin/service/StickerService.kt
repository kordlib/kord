package dev.kord.rest.service

import dev.kord.common.entity.DiscordMessageSticker
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.NamedFile
import dev.kord.rest.Stickers
import dev.kord.rest.builder.guild.StickerModifyBuilder
import dev.kord.rest.json.request.GuildStickerCreateRequest
import dev.kord.rest.json.request.GuildStickerModifyRequest
import dev.kord.rest.json.response.NitroStickerPacksResponse
import dev.kord.rest.request.setBodyWithFiles
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class StickerService(public val client: HttpClient) {

    public suspend fun getNitroStickerPacks(): NitroStickerPacksResponse =
        client.get(Routes.NitroStickerPacks).body()

    public suspend fun getGuildStickers(guildId: Snowflake): List<DiscordMessageSticker> =
        client.get(Routes.Guilds.ById.Stickers(guildId)).body()

    public suspend fun getSticker(id: Snowflake): DiscordMessageSticker =
        client.get(Routes.Stickers.ById(id)).body()

    public suspend fun getGuildSticker(guildId: Snowflake, id: Snowflake): DiscordMessageSticker =
        client.get(Routes.Guilds.ById.Stickers.ById(guildId, id)).body()

    public suspend fun createGuildSticker(
        guildId: Snowflake,
        request: GuildStickerCreateRequest,
        files: List<NamedFile> = emptyList()
    ): DiscordMessageSticker = client.post(Routes.Guilds.ById.Stickers(guildId)) {
        setBodyWithFiles(request, files)
    }.body()

    public suspend fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        request: GuildStickerModifyRequest,
    ): DiscordMessageSticker = client.patch(Routes.Guilds.ById.Stickers.ById(guildId, id)) {
        setBodyWithFiles(request)
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
