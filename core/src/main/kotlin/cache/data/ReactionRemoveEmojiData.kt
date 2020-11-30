package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.DiscordRemovedEmoji
import dev.kord.gateway.DiscordRemovedReactionEmoji
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