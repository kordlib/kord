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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createEmoji(
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

    suspend fun createEmoji(guildId: Snowflake, emoji: EmojiCreateRequest, reason: String? = null) =
        call(Route.GuildEmojiPost) {
            keys[Route.GuildId] = guildId
            body(EmojiCreateRequest.serializer(), emoji)
            auditLogReason(reason)
        }

    suspend fun deleteEmoji(guildId: Snowflake, emojiId: Snowflake, reason: String? = null) =
        call(Route.GuildEmojiDelete) {
            keys[Route.GuildId] = guildId
            keys[Route.EmojiId] = emojiId
            auditLogReason(reason)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyEmoji(
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

    suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake) = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun getEmojis(guildId: Snowflake) = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}
