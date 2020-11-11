package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.gateway.DiscordRemovedEmoji
import com.gitlab.kordlib.gateway.DiscordRemovedReactionEmoji
import kotlinx.serialization.Serializable

@Serializable
data class RemovedReactionData(val id: Snowflake? = null, val name: String?) {
    companion object {
        fun from(entity: DiscordRemovedReactionEmoji): RemovedReactionData = with(entity) {
            RemovedReactionData(id, name)
        }
    }
}

@Serializable
data class ReactionRemoveEmojiData(
        val channelId: Snowflake,
        val guildId: Snowflake,
        val messageId: Snowflake,
        val emoji: RemovedReactionData
) {

    companion object {
        fun from(entity: DiscordRemovedEmoji): ReactionRemoveEmojiData = with(entity) {
            ReactionRemoveEmojiData(channelId, guildId, messageId, RemovedReactionData.from(emoji))
        }
    }

}