package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.EmojiCreatePostRequest
import com.gitlab.kordlib.rest.json.request.EmojiModifyPatchRequest
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {
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

    suspend fun modifyEmoji(guildId: String, emojiId: String, emoji: EmojiModifyRequest, reason: String? = null) = call(Route.GuildEmojiPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        body(EmojiModifyRequest.serializer(), emoji)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun getEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun getEmojis(guildId: String) = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}