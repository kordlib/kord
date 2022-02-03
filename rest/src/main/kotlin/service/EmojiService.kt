package dev.kord.rest.service

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image
import dev.kord.rest.builder.guild.EmojiCreateBuilder
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import dev.kord.rest.json.request.EmojiCreateRequest
import dev.kord.rest.json.request.EmojiModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend inline fun createEmoji(
        guildId: Snowflake,
        name: String,
        image: Image,
        builder: EmojiCreateBuilder.() -> Unit = {}
    ): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.GuildEmojiPost) {
            keys[Route.GuildId] = guildId
            val emoji = EmojiCreateBuilder(name, image).apply(builder)

            body(EmojiCreateRequest.serializer(), emoji.toRequest())
            auditLogReason(emoji.reason)
        }
    }

    public suspend fun createEmoji(
        guildId: Snowflake,
        emoji: EmojiCreateRequest,
        reason: String? = null,
    ): DiscordEmoji = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        body(EmojiCreateRequest.serializer(), emoji)
        auditLogReason(reason)
    }

    public suspend fun deleteEmoji(guildId: Snowflake, emojiId: Snowflake, reason: String? = null): Unit =
        call(Route.GuildEmojiDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.EmojiId] = emojiId
            auditLogReason(reason)
        }

    public suspend inline fun modifyEmoji(
        guildId: Snowflake,
        emojiId: Snowflake,
        builder: EmojiModifyBuilder.() -> Unit
    ): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.GuildEmojiPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.EmojiId] = emojiId
            val modifyBuilder = EmojiModifyBuilder().apply(builder)
            body(EmojiModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    public suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): DiscordEmoji = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    public suspend fun getEmojis(guildId: Snowflake): List<DiscordEmoji> = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}
