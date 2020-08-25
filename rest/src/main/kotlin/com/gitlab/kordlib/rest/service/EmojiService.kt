package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.builder.guild.EmojiCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.EmojiModifyBuilder
import com.gitlab.kordlib.rest.json.request.EmojiCreateRequest
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @OptIn(KordUnstableApi::class)
    suspend inline fun createEmoji(guildId: String, builder: EmojiCreateBuilder.() -> Unit) = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        val emoji = EmojiCreateBuilder().apply(builder)

        body(EmojiCreateRequest.serializer(), emoji.toRequest())
        emoji.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    @OptIn(KordUnstableApi::class)
    @Deprecated("use the inline builder instead",  ReplaceWith("createEmoji(guildId) {  }") ,level = DeprecationLevel.WARNING)
    suspend fun createEmoji(guildId: String, emoji: EmojiCreateRequest, reason: String? = null) = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        body(EmojiCreateRequest.serializer(), emoji)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun deleteEmoji(guildId: String, emojiId: String, reason: String? = null) = call(Route.GuildEmojiDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    @OptIn(KordUnstableApi::class)
    suspend inline fun modifyEmoji(guildId: String, emojiId: String, builder: EmojiModifyBuilder.() -> Unit) = call(Route.GuildEmojiPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        val modifyBuilder = EmojiModifyBuilder().apply(builder)
        body(EmojiModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun getEmojis(guildId: String) = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}