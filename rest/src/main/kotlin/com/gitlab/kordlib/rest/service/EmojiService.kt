package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.entity.DiscordEmoji
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.guild.EmojiCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.EmojiModifyBuilder
import com.gitlab.kordlib.rest.json.request.EmojiCreateRequest
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createEmoji(guildId: Snowflake, builder: EmojiCreateBuilder.() -> Unit): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.GuildEmojiPost) {
            keys[Route.GuildId] = guildId
            val emoji = EmojiCreateBuilder().apply(builder)

            body(EmojiCreateRequest.serializer(), emoji.toRequest())
            emoji.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    @Deprecated("use the inline builder instead", ReplaceWith("createEmoji(guildId) {  }"), level = DeprecationLevel.WARNING)
    suspend fun createEmoji(guildId: Snowflake, emoji: EmojiCreateRequest, reason: String? = null) = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        body(EmojiCreateRequest.serializer(), emoji)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun deleteEmoji(guildId: Snowflake, emojiId: Snowflake, reason: String? = null) = call(Route.GuildEmojiDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyEmoji(guildId: Snowflake, emojiId: Snowflake, builder: EmojiModifyBuilder.() -> Unit): DiscordEmoji {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.GuildEmojiPatch) {
            keys[Route.GuildId] = guildId
            keys[Route.EmojiId] = emojiId
            val modifyBuilder = EmojiModifyBuilder().apply(builder)
            body(EmojiModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
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