package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.EmojiCreateRequest
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route

class EmojiService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createEmoji(guildId: String, emoji: com.gitlab.kordlib.rest.json.request.EmojiCreateRequest) = call(Route.GuildEmojiPost) {
        keys[Route.GuildId] = guildId
        body(com.gitlab.kordlib.rest.json.request.EmojiCreateRequest.serializer(), emoji)
    }

    suspend fun deleteEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun modifyEmoji(guildId: String, emojiId: String, emoji: com.gitlab.kordlib.rest.json.request.EmojiModifyRequest) = call(Route.GuildEmojiPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
        body(com.gitlab.kordlib.rest.json.request.EmojiModifyRequest.serializer(), emoji)
    }

    suspend fun getEmoji(guildId: String, emojiId: String) = call(Route.GuildEmojiGet) {
        keys[Route.GuildId] = guildId
        keys[Route.EmojiId] = emojiId
    }

    suspend fun getEmojis(guildId: String) = call(Route.GuildEmojisGet) {
        keys[Route.GuildId] = guildId
    }
}