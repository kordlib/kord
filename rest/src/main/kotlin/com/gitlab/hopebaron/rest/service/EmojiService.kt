package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.json.request.EmojiCreateRequest
import com.gitlab.hopebaron.rest.json.request.EmojiModifyRequest
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createEmoji(guildId: String, emoji: EmojiCreateRequest) = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        body(EmojiCreateRequest.serializer(), emoji)
    }

    suspend fun deleteEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun modifyEmoji(guildId: String, emojiId: String, emoji: EmojiModifyRequest) = call(Route.GuildEmojiPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        body(EmojiModifyRequest.serializer(), emoji)
    }

    suspend fun getEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun getEmojis(guildId: String) = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}