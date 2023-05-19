package dev.kord.rest.service

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.Emojis
import dev.kord.rest.Image
import dev.kord.rest.builder.guild.EmojiCreateBuilder
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import dev.kord.rest.json.request.EmojiCreateRequest
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class EmojiService(public val client: HttpClient) {

    public suspend inline fun createEmoji(
        guildId: Snowflake,
        name: String,
        image: Image,
        builder: EmojiCreateBuilder.() -> Unit = {}
    ): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return client.post(Routes.Guilds.ById.Emojis(guildId)) {
            val emoji = EmojiCreateBuilder(name, image).apply(builder)

            setBody(emoji.toRequest())
            auditLogReason(emoji.reason)
        }.body()
    }

    public suspend fun createEmoji(
        guildId: Snowflake,
        request: EmojiCreateRequest,
        reason: String? = null,
    ): DiscordEmoji =
        client.post(Routes.Guilds.ById.Emojis(guildId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend fun deleteEmoji(guildId: Snowflake, emojiId: Snowflake, reason: String? = null): Unit {
        client.delete(Routes.Guilds.ById.Emojis.ById(guildId, emojiId)) {
            auditLogReason(reason)
        }
    }

    public suspend inline fun modifyEmoji(
        guildId: Snowflake,
        emojiId: Snowflake,
        builder: EmojiModifyBuilder.() -> Unit
    ): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return client.patch(Routes.Guilds.ById.Emojis.ById(guildId, emojiId)) {
            val modifyBuilder = EmojiModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): DiscordEmoji =
        client.get(Routes.Guilds.ById.Emojis.ById(guildId, emojiId)).body()

    public suspend fun getEmojis(guildId: Snowflake): List<DiscordEmoji> =
        client.get(Routes.Guilds.ById.Emojis(guildId)).body()

}
